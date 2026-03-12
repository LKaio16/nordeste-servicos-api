package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.NotaFiscalRequestDTO;
import com.codagis.nordeste_servicos.dto.NotaFiscalResponseDTO;
import com.codagis.nordeste_servicos.exception.BusinessException;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.*;
import com.codagis.nordeste_servicos.repository.ClienteRepository;
import com.codagis.nordeste_servicos.repository.FornecedorRepository;
import com.codagis.nordeste_servicos.repository.NotaFiscalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotaFiscalService {

    @Autowired
    private NotaFiscalRepository notaFiscalRepository;
    @Autowired
    private FornecedorRepository fornecedorRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<NotaFiscalResponseDTO> findAll(Long fornecedorId, Long clienteId, String tipo) {
        List<NotaFiscal> list;
        if (fornecedorId != null) {
            list = notaFiscalRepository.findByFornecedorId(fornecedorId);
        } else if (clienteId != null) {
            list = notaFiscalRepository.findByClienteId(clienteId);
        } else if (tipo != null && !tipo.isBlank()) {
            try {
                list = notaFiscalRepository.findByTipo(TipoNotaFiscal.valueOf(tipo));
            } catch (IllegalArgumentException e) {
                list = notaFiscalRepository.findAll();
            }
        } else {
            list = notaFiscalRepository.findAll();
        }
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotaFiscalResponseDTO findById(Long id) {
        NotaFiscal n = notaFiscalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota fiscal não encontrada com ID: " + id));
        return toResponse(n);
    }

    @Transactional
    public NotaFiscalResponseDTO create(NotaFiscalRequestDTO dto) {
        String numero = dto.getNumeroNota() != null ? dto.getNumeroNota().trim() : "";
        if (numero.isEmpty()) {
            throw new BusinessException("Número da nota é obrigatório.");
        }
        if (notaFiscalRepository.existsByNumeroNota(numero)) {
            throw new BusinessException("Número de nota fiscal já cadastrado.");
        }

        NotaFiscal n = new NotaFiscal();
        mapDtoToEntity(dto, n);
        n = notaFiscalRepository.save(n);
        return toResponse(n);
    }

    @Transactional
    public NotaFiscalResponseDTO update(Long id, NotaFiscalRequestDTO dto) {
        NotaFiscal n = notaFiscalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota fiscal não encontrada com ID: " + id));
        String numero = dto.getNumeroNota() != null ? dto.getNumeroNota().trim() : "";
        if (!n.getNumeroNota().equals(numero) && notaFiscalRepository.existsByNumeroNota(numero)) {
            throw new BusinessException("Número de nota fiscal já cadastrado.");
        }

        mapDtoToEntity(dto, n);
        n = notaFiscalRepository.save(n);
        return toResponse(n);
    }

    @Transactional
    public void delete(Long id) {
        if (!notaFiscalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nota fiscal não encontrada com ID: " + id);
        }
        notaFiscalRepository.deleteById(id);
    }

    private void mapDtoToEntity(NotaFiscalRequestDTO dto, NotaFiscal n) {
        n.setTipo(dto.getTipo());
        n.setFornecedor(null);
        if (dto.getFornecedorId() != null) {
            n.setFornecedor(fornecedorRepository.findById(dto.getFornecedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com ID: " + dto.getFornecedorId())));
        }
        n.setCliente(null);
        if (dto.getClienteId() != null) {
            n.setCliente(clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + dto.getClienteId())));
        }
        n.setNomeEmitente(dto.getNomeEmitente());
        n.setCnpjEmitente(dto.getCnpjEmitente());
        n.setDataEmissao(dto.getDataEmissao());
        n.setNumeroNota(dto.getNumeroNota().trim());
        n.setValorTotal(dto.getValorTotal());
        n.setFormaPagamento(dto.getFormaPagamento());
        n.setDescricao(dto.getDescricao());
        n.setObservacoes(dto.getObservacoes());
    }

    private NotaFiscalResponseDTO toResponse(NotaFiscal n) {
        NotaFiscalResponseDTO dto = new NotaFiscalResponseDTO();
        dto.setId(n.getId());
        dto.setTipo(n.getTipo());
        dto.setFornecedorId(n.getFornecedor() != null ? n.getFornecedor().getId() : null);
        dto.setFornecedorNome(n.getFornecedor() != null ? n.getFornecedor().getNome() : null);
        dto.setClienteId(n.getCliente() != null ? n.getCliente().getId() : null);
        dto.setClienteNome(n.getCliente() != null ? n.getCliente().getNomeCompleto() : null);
        dto.setNomeEmitente(n.getNomeEmitente());
        dto.setCnpjEmitente(n.getCnpjEmitente());
        dto.setDataEmissao(n.getDataEmissao());
        dto.setNumeroNota(n.getNumeroNota());
        dto.setValorTotal(n.getValorTotal());
        dto.setFormaPagamento(n.getFormaPagamento());
        dto.setDescricao(n.getDescricao());
        dto.setObservacoes(n.getObservacoes());
        return dto;
    }
}
