package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.OrcamentoRequestDTO;
import com.codagis.nordeste_servicos.dto.OrcamentoResponseDTO;
import com.codagis.nordeste_servicos.exception.BusinessException;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.Cliente;
import com.codagis.nordeste_servicos.model.Orcamento;
import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.model.StatusOrcamento;
import com.codagis.nordeste_servicos.repository.ClienteRepository;
import com.codagis.nordeste_servicos.repository.OrcamentoRepository;
import com.codagis.nordeste_servicos.repository.OrdemServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository orçamentoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    // TODO: Precisaremos de um serviço para ItemOrcamento para calcular o total

    public List<OrcamentoResponseDTO> findAllOrcamentos() {
        List<Orcamento> orçamentos = orçamentoRepository.findAll();
        return orçamentos.stream()
                          .map(this::convertToDTO)
                          .collect(Collectors.toList());
    }

    public List<OrcamentoResponseDTO> findOrcamentosByClienteId(Long clienteId) {
        List<Orcamento> orçamentos = orçamentoRepository.findByClienteId(clienteId);
        return orçamentos.stream()
                          .map(this::convertToDTO)
                          .collect(Collectors.toList());
    }

    public List<OrcamentoResponseDTO> findOrcamentosByStatus(StatusOrcamento status) {
        List<Orcamento> orçamentos = orçamentoRepository.findByStatus(status);
        return orçamentos.stream()
                          .map(this::convertToDTO)
                          .collect(Collectors.toList());
    }

    public Optional<OrcamentoResponseDTO> findOrcamentoByOrdemServicoId(Long ordemServicoId) {
        Optional<Orcamento> orçamento = orçamentoRepository.findByOrdemServicoOrigemId(ordemServicoId);
        return orçamento.map(this::convertToDTO);
    }


    public OrcamentoResponseDTO findOrcamentoById(Long id) {
        Orcamento orçamento = orçamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado com ID: " + id));
        return convertToDTO(orçamento);
    }

    // Método para criar um novo orçamento
    public OrcamentoResponseDTO createOrcamento(OrcamentoRequestDTO orçamentoRequestDTO) {
        Cliente cliente = clienteRepository.findById(orçamentoRequestDTO.getClienteId())
                 .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + orçamentoRequestDTO.getClienteId()));

        OrdemServico ordemServicoOrigem = null;
        if (orçamentoRequestDTO.getOrdemServicoOrigemId() != null) {
            ordemServicoOrigem = ordemServicoRepository.findById(orçamentoRequestDTO.getOrdemServicoOrigemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço de origem não encontrada com ID: " + orçamentoRequestDTO.getOrdemServicoOrigemId()));
            // TODO: Opcional: Validar se já existe orçamento para esta OS
        }

        Orcamento orçamento = convertToEntity(orçamentoRequestDTO);
        orçamento.setNumeroOrcamento(generateNumeroOrcamento()); // TODO: Implementar lógica de geração de número único
        orçamento.setDataCriacao(LocalDate.now());
        orçamento.setCliente(cliente);
        orçamento.setOrdemServicoOrigem(ordemServicoOrigem);
        // status, observacoesCondicoes, dataValidade já vêm do DTO
        // valorTotal será calculado ao adicionar itens

        Orcamento savedOrcamento = orçamentoRepository.save(orçamento);
        return convertToDTO(savedOrcamento);
    }

     // Método para atualizar um orçamento existente
    public OrcamentoResponseDTO updateOrcamento(Long id, OrcamentoRequestDTO orçamentoRequestDTO) {
         Orcamento existingOrcamento = orçamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado com ID: " + id));

         Cliente cliente = clienteRepository.findById(orçamentoRequestDTO.getClienteId())
                 .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + orçamentoRequestDTO.getClienteId()));

         OrdemServico ordemServicoOrigem = null;
        if (orçamentoRequestDTO.getOrdemServicoOrigemId() != null) {
            ordemServicoOrigem = ordemServicoRepository.findById(orçamentoRequestDTO.getOrdemServicoOrigemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço de origem não encontrada com ID: " + orçamentoRequestDTO.getOrdemServicoOrigemId()));
             // TODO: Opcional: Validar se já existe orçamento para esta OS (se o ID da OS mudou)
        }


         existingOrcamento.setCliente(cliente);
         existingOrcamento.setOrdemServicoOrigem(ordemServicoOrigem);
         existingOrcamento.setDataValidade(orçamentoRequestDTO.getDataValidade());
         existingOrcamento.setObservacoesCondicoes(orçamentoRequestDTO.getObservacoesCondicoes());
         existingOrcamento.setStatus(orçamentoRequestDTO.getStatus());
         // TODO: Recalcular valorTotal se os itens forem atualizados (gerenciado pelo ItemOrcamentoService)


         Orcamento updatedOrcamento = orçamentoRepository.save(existingOrcamento);
         return convertToDTO(updatedOrcamento);
    }


    public void deleteOrcamento(Long id) {
        if (!orçamentoRepository.existsById(id)) {
             throw new ResourceNotFoundException("Orçamento não encontrado com ID: " + id);
        }
         // TODO: Implementar lógica para remover ItemOrcamento relacionados
        orçamentoRepository.deleteById(id);
    }

     // TODO: Implementar lógica para geração de número de Orçamento único
    private String generateNumeroOrcamento() {
        return "ORC-" + System.currentTimeMillis(); // Exemplo simples baseado em timestamp
    }

     // Método para calcular e atualizar o valor total do orçamento (chamado pelo ItemOrcamentoService)
    public void recalcularValorTotal(Long orcamentoId) {
        Orcamento orçamento = orçamentoRepository.findById(orcamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado com ID: " + orcamentoId));

        // TODO: Chamar o ItemOrcamentoService para somar os subtotais dos itens
        // double total = itemOrcamentoService.calcularTotalItens(orcamentoId);
        double total = 0.0; // Placeholder
        orçamento.setValorTotal(total);
        orçamentoRepository.save(orçamento);
    }


    private OrcamentoResponseDTO convertToDTO(Orcamento orçamento) {
        OrcamentoResponseDTO dto = new OrcamentoResponseDTO();
        dto.setId(orçamento.getId());
        dto.setNumeroOrcamento(orçamento.getNumeroOrcamento());
        dto.setDataCriacao(orçamento.getDataCriacao());
        dto.setDataValidade(orçamento.getDataValidade());
        dto.setStatus(orçamento.getStatus());

        dto.setClienteId(orçamento.getCliente().getId());
        dto.setNomeCliente(orçamento.getCliente().getNomeCompleto()); // Popula nome do cliente
        if (orçamento.getOrdemServicoOrigem() != null) {
             dto.setOrdemServicoOrigemId(orçamento.getOrdemServicoOrigem().getId());
        }

        dto.setObservacoesCondicoes(orçamento.getObservacoesCondicoes());
        dto.setValorTotal(orçamento.getValorTotal());

        return dto;
    }

    // Método para converter DTO para Entidade (usado na criação/atualização)
    private Orcamento convertToEntity(OrcamentoRequestDTO orçamentoRequestDTO) {
        Orcamento orçamento = new Orcamento();
         // Numero, dataCriacao, cliente, ordemServicoOrigem definidos no serviço
         orçamento.setDataValidade(orçamentoRequestDTO.getDataValidade());
         orçamento.setObservacoesCondicoes(orçamentoRequestDTO.getObservacoesCondicoes());
         orçamento.setStatus(orçamentoRequestDTO.getStatus());
        // valorTotal definido no serviço
        return orçamento;
    }
}