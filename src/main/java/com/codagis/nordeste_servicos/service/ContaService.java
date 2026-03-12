package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.ContaRequestDTO;
import com.codagis.nordeste_servicos.dto.ContaResponseDTO;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.*;
import com.codagis.nordeste_servicos.repository.ClienteRepository;
import com.codagis.nordeste_servicos.repository.ContaRepository;
import com.codagis.nordeste_servicos.repository.FornecedorRepository;
import com.codagis.nordeste_servicos.repository.ParcelaRepository;
import com.codagis.nordeste_servicos.service.ParcelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private FornecedorRepository fornecedorRepository;
    @Autowired
    private ParcelaRepository parcelaRepository;
    @Autowired
    private ParcelaService parcelaService;

    @Transactional(readOnly = true)
    public List<ContaResponseDTO> findAll(Long clienteId, Long fornecedorId, String tipo, String status) {
        List<Conta> list;
        if (clienteId != null) {
            list = contaRepository.findByClienteId(clienteId);
        } else if (fornecedorId != null) {
            list = contaRepository.findByFornecedorId(fornecedorId);
        } else if (tipo != null && status != null) {
            try {
                list = contaRepository.findByTipoAndStatus(TipoConta.valueOf(tipo), StatusConta.valueOf(status));
            } catch (IllegalArgumentException e) {
                list = contaRepository.findAll();
            }
        } else if (tipo != null) {
            try {
                list = contaRepository.findByTipo(TipoConta.valueOf(tipo));
            } catch (IllegalArgumentException e) {
                list = contaRepository.findAll();
            }
        } else if (status != null) {
            try {
                list = contaRepository.findByStatus(StatusConta.valueOf(status));
            } catch (IllegalArgumentException e) {
                list = contaRepository.findAll();
            }
        } else {
            list = contaRepository.findAll();
        }
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContaResponseDTO findById(Long id) {
        Conta c = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com ID: " + id));
        return toResponse(c);
    }

    @Transactional
    public ContaResponseDTO create(ContaRequestDTO dto) {
        Conta c = new Conta();
        mapDtoToEntity(dto, c);
        atualizarStatusConta(c);
        c = contaRepository.save(c);
        return toResponse(c);
    }

    @Transactional
    public ContaResponseDTO update(Long id, ContaRequestDTO dto) {
        Conta c = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com ID: " + id));
        mapDtoToEntity(dto, c);
        atualizarStatusConta(c);
        c = contaRepository.save(c);
        if (c.getStatus() == StatusConta.PAGO) {
            parcelaService.marcarTodasComoPagas(c.getId());
        }
        return toResponse(c);
    }

    @Transactional
    public void delete(Long id) {
        if (!contaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Conta não encontrada com ID: " + id);
        }
        parcelaRepository.deleteByConta_Id(id);
        parcelaRepository.flush();
        try {
            contaRepository.deleteById(id);
            contaRepository.flush();
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ResourceNotFoundException("Conta não encontrada ou já foi excluída com ID: " + id);
        }
    }

    @Transactional
    public ContaResponseDTO marcarComoPaga(Long id, LocalDate dataPagamento, String formaPagamento) {
        Conta c = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com ID: " + id));
        c.setStatus(StatusConta.PAGO);
        c.setDataPagamento(dataPagamento != null ? dataPagamento : LocalDate.now());
        c.setValorPago(c.getValor());
        if (formaPagamento != null && !formaPagamento.isBlank()) {
            try {
                c.setFormaPagamento(FormaPagamento.valueOf(formaPagamento.toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
        c = contaRepository.save(c);
        parcelaService.marcarTodasComoPagas(c.getId());
        return toResponse(c);
    }

    private void mapDtoToEntity(ContaRequestDTO dto, Conta c) {
        c.setTipo(dto.getTipo());
        c.setCliente(null);
        if (dto.getClienteId() != null) {
            c.setCliente(clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + dto.getClienteId())));
        }
        c.setFornecedor(null);
        if (dto.getFornecedorId() != null) {
            c.setFornecedor(fornecedorRepository.findById(dto.getFornecedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com ID: " + dto.getFornecedorId())));
        }
        c.setDescricao(dto.getDescricao());
        c.setValor(dto.getValor());
        if (dto.getStatus() == StatusConta.PAGO) {
            c.setValorPago(dto.getValor() != null ? dto.getValor() : BigDecimal.ZERO);
        } else {
            c.setValorPago(dto.getValorPago() != null ? dto.getValorPago() : BigDecimal.ZERO);
        }
        c.setDataVencimento(dto.getDataVencimento());
        c.setDataPagamento(dto.getDataPagamento());
        c.setStatus(dto.getStatus());
        c.setCategoria(dto.getCategoria() != null && !dto.getCategoria().isBlank() ? dto.getCategoria() : "Geral");
        c.setCategoriaFinanceira(dto.getCategoriaFinanceira());
        c.setSubcategoria(dto.getSubcategoria());
        c.setFormaPagamento(dto.getFormaPagamento());
        c.setObservacoes(dto.getObservacoes());
    }

    private void atualizarStatusConta(Conta c) {
        if (c.getStatus() == StatusConta.PAGO) return;
        if (c.getDataVencimento().isBefore(LocalDate.now()) && c.getStatus() == StatusConta.PENDENTE) {
            c.setStatus(StatusConta.VENCIDO);
        }
    }

    private ContaResponseDTO toResponse(Conta c) {
        ContaResponseDTO dto = new ContaResponseDTO();
        dto.setId(c.getId());
        dto.setTipo(c.getTipo());
        dto.setClienteId(c.getCliente() != null ? c.getCliente().getId() : null);
        dto.setClienteNome(c.getCliente() != null ? c.getCliente().getNomeCompleto() : null);
        dto.setFornecedorId(c.getFornecedor() != null ? c.getFornecedor().getId() : null);
        dto.setFornecedorNome(c.getFornecedor() != null ? c.getFornecedor().getNome() : null);
        dto.setDescricao(c.getDescricao());
        dto.setValor(c.getValor());
        dto.setValorPago(c.getValorPago() != null ? c.getValorPago() : BigDecimal.ZERO);
        dto.setDataVencimento(c.getDataVencimento());
        dto.setDataPagamento(c.getDataPagamento());
        dto.setStatus(c.getStatus());
        dto.setCategoria(c.getCategoria());
        dto.setCategoriaFinanceira(c.getCategoriaFinanceira());
        dto.setSubcategoria(c.getSubcategoria());
        dto.setFormaPagamento(c.getFormaPagamento());
        dto.setObservacoes(c.getObservacoes());
        return dto;
    }
}
