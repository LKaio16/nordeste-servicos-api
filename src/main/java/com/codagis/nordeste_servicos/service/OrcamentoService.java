package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.OrcamentoRequestDTO;
import com.codagis.nordeste_servicos.dto.OrcamentoResponseDTO;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.*;
import com.codagis.nordeste_servicos.repository.ClienteRepository;
import com.codagis.nordeste_servicos.repository.ItemOrcamentoRepository; // IMPORT ADICIONADO
import com.codagis.nordeste_servicos.repository.OrcamentoRepository;
import com.codagis.nordeste_servicos.repository.OrdemServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // IMPORT ADICIONADO

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    // INJEÇÃO DE DEPENDÊNCIA ADICIONADA
    @Autowired
    private ItemOrcamentoRepository itemOrcamentoRepository;

    @Transactional(readOnly = true)
    public List<OrcamentoResponseDTO> findAllOrcamentos() {
        List<Orcamento> orçamentos = orcamentoRepository.findAll();
        return orçamentos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrcamentoResponseDTO> findOrcamentosByClienteId(Long clienteId) {
        List<Orcamento> orçamentos = orcamentoRepository.findByClienteId(clienteId);
        return orçamentos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrcamentoResponseDTO> findOrcamentosByStatus(StatusOrcamento status) {
        List<Orcamento> orçamentos = orcamentoRepository.findByStatus(status);
        return orçamentos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<OrcamentoResponseDTO> findOrcamentoByOrdemServicoId(Long ordemServicoId) {
        Optional<Orcamento> orçamento = orcamentoRepository.findByOrdemServicoOrigemId(ordemServicoId);
        return orçamento.map(this::convertToDTO);
    }

    /**
     * MÉTODO MODIFICADO
     * Agora, ele recalcula o valor total ANTES de retornar os dados.
     */
    @Transactional
    public OrcamentoResponseDTO findOrcamentoById(Long id) {
        // 1. Recalcula e salva o valor total no banco de dados.
        recalcularEAtualizarValorTotal(id);

        // 2. Busca o orçamento (agora com o valor atualizado) para retornar.
        Orcamento orçamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado com ID: " + id));

        return convertToDTO(orçamento);
    }

    public OrcamentoResponseDTO createOrcamento(OrcamentoRequestDTO orçamentoRequestDTO) {
        Cliente cliente = clienteRepository.findById(orçamentoRequestDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + orçamentoRequestDTO.getClienteId()));

        OrdemServico ordemServicoOrigem = null;
        if (orçamentoRequestDTO.getOrdemServicoOrigemId() != null) {
            ordemServicoOrigem = ordemServicoRepository.findById(orçamentoRequestDTO.getOrdemServicoOrigemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço de origem não encontrada com ID: " + orçamentoRequestDTO.getOrdemServicoOrigemId()));
        }

        Orcamento orçamento = convertToEntity(orçamentoRequestDTO);
        orçamento.setNumeroOrcamento(generateNumeroOrcamento());
        orçamento.setDataCriacao(LocalDate.now());
        orçamento.setCliente(cliente);
        orçamento.setOrdemServicoOrigem(ordemServicoOrigem);
        orçamento.setValorTotal(0.0); // O valor inicial é 0.0

        Orcamento savedOrcamento = orcamentoRepository.save(orçamento);
        return convertToDTO(savedOrcamento);
    }

    public OrcamentoResponseDTO updateOrcamento(Long id, OrcamentoRequestDTO orçamentoRequestDTO) {
        Orcamento existingOrcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado com ID: " + id));

        Cliente cliente = clienteRepository.findById(orçamentoRequestDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + orçamentoRequestDTO.getClienteId()));

        OrdemServico ordemServicoOrigem = null;
        if (orçamentoRequestDTO.getOrdemServicoOrigemId() != null) {
            ordemServicoOrigem = ordemServicoRepository.findById(orçamentoRequestDTO.getOrdemServicoOrigemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço de origem não encontrada com ID: " + orçamentoRequestDTO.getOrdemServicoOrigemId()));
        }

        existingOrcamento.setCliente(cliente);
        existingOrcamento.setOrdemServicoOrigem(ordemServicoOrigem);
        existingOrcamento.setDataValidade(orçamentoRequestDTO.getDataValidade());
        existingOrcamento.setObservacoesCondicoes(orçamentoRequestDTO.getObservacoesCondicoes());
        existingOrcamento.setStatus(orçamentoRequestDTO.getStatus());

        Orcamento updatedOrcamento = orcamentoRepository.save(existingOrcamento);
        return convertToDTO(updatedOrcamento);
    }

    public void deleteOrcamento(Long id) {
        if (!orcamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Orçamento não encontrado com ID: " + id);
        }
        orcamentoRepository.deleteById(id);
    }

    /**
     * NOVO MÉTODO ADICIONADO
     * Responsável por recalcular o valor total de um orçamento.
     */
    @Transactional
    public void recalcularEAtualizarValorTotal(Long orcamentoId) {
        Orcamento orcamento = orcamentoRepository.findById(orcamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Tentativa de recalcular um orçamento inexistente. ID: " + orcamentoId));

        List<ItemOrcamento> itens = itemOrcamentoRepository.findByOrcamentoId(orcamentoId);

        double novoTotal = itens.stream()
                .mapToDouble(item -> item.getSubtotal() != null ? item.getSubtotal() : 0.0)
                .sum();

        orcamento.setValorTotal(novoTotal);

        orcamentoRepository.saveAndFlush(orcamento);
    }

    private String generateNumeroOrcamento() {
        return "ORC-" + System.currentTimeMillis();
    }

    private OrcamentoResponseDTO convertToDTO(Orcamento orçamento) {
        OrcamentoResponseDTO dto = new OrcamentoResponseDTO();
        dto.setId(orçamento.getId());
        dto.setNumeroOrcamento(orçamento.getNumeroOrcamento());
        dto.setDataCriacao(orçamento.getDataCriacao());
        dto.setDataValidade(orçamento.getDataValidade());
        dto.setStatus(orçamento.getStatus());
        dto.setClienteId(orçamento.getCliente().getId());
        dto.setNomeCliente(orçamento.getCliente().getNomeCompleto());
        if (orçamento.getOrdemServicoOrigem() != null) {
            dto.setOrdemServicoOrigemId(orçamento.getOrdemServicoOrigem().getId());
        }
        dto.setObservacoesCondicoes(orçamento.getObservacoesCondicoes());
        dto.setValorTotal(orçamento.getValorTotal());
        return dto;
    }

    private Orcamento convertToEntity(OrcamentoRequestDTO orçamentoRequestDTO) {
        Orcamento orçamento = new Orcamento();
        orçamento.setDataValidade(orçamentoRequestDTO.getDataValidade());
        orçamento.setObservacoesCondicoes(orçamentoRequestDTO.getObservacoesCondicoes());
        orçamento.setStatus(orçamentoRequestDTO.getStatus());
        return orçamento;
    }
}