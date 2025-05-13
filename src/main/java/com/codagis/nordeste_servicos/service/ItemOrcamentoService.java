package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.ItemOrcamentoRequestDTO;
import com.codagis.nordeste_servicos.dto.ItemOrcamentoResponseDTO;
import com.codagis.nordeste_servicos.exception.BusinessException;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.ItemOrcamento;
import com.codagis.nordeste_servicos.model.Orcamento;
import com.codagis.nordeste_servicos.model.PecaMaterial;
import com.codagis.nordeste_servicos.model.TipoServico;
import com.codagis.nordeste_servicos.repository.ItemOrcamentoRepository;
import com.codagis.nordeste_servicos.repository.OrcamentoRepository;
import com.codagis.nordeste_servicos.repository.PecaMaterialRepository;
import com.codagis.nordeste_servicos.repository.TipoServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemOrcamentoService {

    private final ItemOrcamentoRepository itemOrcamentoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final PecaMaterialRepository pecaMaterialRepository;
    private final TipoServicoRepository tipoServicoRepository;
    private final OrcamentoService orcamentoService; // Injeção para chamar o método de recalcularTotal


    @Autowired
    public ItemOrcamentoService(ItemOrcamentoRepository itemOrcamentoRepository,
                                 OrcamentoRepository orcamentoRepository,
                                 PecaMaterialRepository pecaMaterialRepository,
                                 TipoServicoRepository tipoServicoRepository,
                                 OrcamentoService orcamentoService) {
        this.itemOrcamentoRepository = itemOrcamentoRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.pecaMaterialRepository = pecaMaterialRepository;
        this.tipoServicoRepository = tipoServicoRepository;
        this.orcamentoService = orcamentoService;
    }


    // Método para listar itens de um orçamento específico
    public List<ItemOrcamentoResponseDTO> findItensByOrcamentoId(Long orcamentoId) {
        List<ItemOrcamento> itens = itemOrcamentoRepository.findByOrcamentoId(orcamentoId);
        return itens.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
    }

     public ItemOrcamentoResponseDTO findItemOrcamentoById(Long id) {
         ItemOrcamento item = itemOrcamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do Orçamento não encontrado com ID: " + id));
         return convertToDTO(item);
     }


    // Método para adicionar um novo item ao orçamento
    @Transactional // Garante que as operações de salvar o item e recalcular o total sejam atômicas
    public ItemOrcamentoResponseDTO createItemOrcamento(ItemOrcamentoRequestDTO itemOrcamentoRequestDTO) {
        Orcamento orcamento = orcamentoRepository.findById(itemOrcamentoRequestDTO.getOrcamentoId())
                 .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado com ID: " + itemOrcamentoRequestDTO.getOrcamentoId()));

        PecaMaterial pecaMaterial = null;
        if (itemOrcamentoRequestDTO.getPecaMaterialId() != null) {
             pecaMaterial = pecaMaterialRepository.findById(itemOrcamentoRequestDTO.getPecaMaterialId())
                 .orElseThrow(() -> new ResourceNotFoundException("Peça/Material não encontrado com ID: " + itemOrcamentoRequestDTO.getPecaMaterialId()));
        }

        TipoServico tipoServico = null;
        if (itemOrcamentoRequestDTO.getTipoServicoId() != null) {
             tipoServico = tipoServicoRepository.findById(itemOrcamentoRequestDTO.getTipoServicoId())
                 .orElseThrow(() -> new ResourceNotFoundException("Tipo de Serviço não encontrado com ID: " + itemOrcamentoRequestDTO.getTipoServicoId()));
        }

        // Validações de negócio
        if (pecaMaterial == null && tipoServico == null && (itemOrcamentoRequestDTO.getDescricao() == null || itemOrcamentoRequestDTO.getDescricao().trim().isEmpty())) {
             throw new BusinessException("É necessário informar a descrição ou associar a uma peça/material ou tipo de serviço.");
        }
        if (itemOrcamentoRequestDTO.getQuantidade() == null || itemOrcamentoRequestDTO.getQuantidade() <= 0) {
             throw new BusinessException("A quantidade deve ser maior que zero.");
        }
         if (itemOrcamentoRequestDTO.getValorUnitario() == null || itemOrcamentoRequestDTO.getValorUnitario() < 0) {
             throw new BusinessException("O valor unitário não pode ser negativo.");
        }


        ItemOrcamento novoItem = convertToEntity(itemOrcamentoRequestDTO);
        novoItem.setOrcamento(orcamento);
        novoItem.setPecaMaterial(pecaMaterial);
        novoItem.setTipoServico(tipoServico);
        novoItem.setSubtotal(novoItem.getQuantidade() * novoItem.getValorUnitario()); // Calcula o subtotal

        ItemOrcamento savedItem = itemOrcamentoRepository.save(novoItem);

        // Recalcula o valor total do orçamento pai
        orcamentoService.recalcularValorTotal(orcamento.getId());


        return convertToDTO(savedItem);
    }

     // Método para atualizar um item no orçamento
    @Transactional // Garante que as operações sejam atômicas
    public ItemOrcamentoResponseDTO updateItemOrcamento(Long id, ItemOrcamentoRequestDTO itemOrcamentoRequestDTO) {
         ItemOrcamento existingItem = itemOrcamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do Orçamento não encontrado com ID: " + id));

         Long orcamentoOriginalId = existingItem.getOrcamento().getId(); // Guarda o ID do orçamento original

         Orcamento orcamento = orcamentoRepository.findById(itemOrcamentoRequestDTO.getOrcamentoId())
                 .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado com ID: " + itemOrcamentoRequestDTO.getOrcamentoId()));

        PecaMaterial pecaMaterial = null;
        if (itemOrcamentoRequestDTO.getPecaMaterialId() != null) {
             pecaMaterial = pecaMaterialRepository.findById(itemOrcamentoRequestDTO.getPecaMaterialId())
                 .orElseThrow(() -> new ResourceNotFoundException("Peça/Material não encontrado com ID: " + itemOrcamentoRequestDTO.getPecaMaterialId()));
        }

        TipoServico tipoServico = null;
        if (itemOrcamentoRequestDTO.getTipoServicoId() != null) {
             tipoServico = tipoServicoRepository.findById(itemOrcamentoRequestDTO.getTipoServicoId())
                 .orElseThrow(() -> new ResourceNotFoundException("Tipo de Serviço não encontrado com ID: " + itemOrcamentoRequestDTO.getTipoServicoId()));
        }

        // Validações de negócio (semelhantes à criação)
         if (pecaMaterial == null && tipoServico == null && (itemOrcamentoRequestDTO.getDescricao() == null || itemOrcamentoRequestDTO.getDescricao().trim().isEmpty())) {
             throw new BusinessException("É necessário informar a descrição ou associar a uma peça/material ou tipo de serviço.");
        }
        if (itemOrcamentoRequestDTO.getQuantidade() == null || itemOrcamentoRequestDTO.getQuantidade() <= 0) {
             throw new BusinessException("A quantidade deve ser maior que zero.");
        }
         if (itemOrcamentoRequestDTO.getValorUnitario() == null || itemOrcamentoRequestDTO.getValorUnitario() < 0) {
             throw new BusinessException("O valor unitário não pode ser negativo.");
        }


         existingItem.setOrcamento(orcamento); // Permite mover o item para outro orçamento
         existingItem.setPecaMaterial(pecaMaterial);
         existingItem.setTipoServico(tipoServico);
         existingItem.setDescricao(itemOrcamentoRequestDTO.getDescricao());
         existingItem.setQuantidade(itemOrcamentoRequestDTO.getQuantidade());
         existingItem.setValorUnitario(itemOrcamentoRequestDTO.getValorUnitario());
         existingItem.setSubtotal(existingItem.getQuantidade() * existingItem.getValorUnitario()); // Recalcula o subtotal


         ItemOrcamento updatedItem = itemOrcamentoRepository.save(existingItem);

         // Recalcula o valor total do orçamento pai NOVO
         orcamentoService.recalcularValorTotal(updatedItem.getOrcamento().getId());

         // Se o orçamento pai ORIGINAL for diferente do NOVO, recalcula o total do ORIGINAL também
         if (!orcamentoOriginalId.equals(updatedItem.getOrcamento().getId())) {
             orcamentoService.recalcularValorTotal(orcamentoOriginalId);
         }


        return convertToDTO(updatedItem);
    }


    // Método para deletar um item do orçamento
     @Transactional // Garante que as operações sejam atômicas
    public void deleteItemOrcamento(Long id) {
        ItemOrcamento item = itemOrcamentoRepository.findById(id)
             .orElseThrow(() -> new ResourceNotFoundException("Item do Orçamento não encontrado com ID: " + id));

        Long orcamentoIdAfetado = item.getOrcamento().getId(); // Guarda o ID do orçamento pai

        itemOrcamentoRepository.delete(item);

        // Recalcula o valor total do orçamento pai afetado
        orcamentoService.recalcularValorTotal(orcamentoIdAfetado);

    }

     // Método para calcular o total dos itens para um orçamento específico (chamado pelo OrcamentoService)
     public double calcularTotalItens(Long orcamentoId) {
         List<ItemOrcamento> itens = itemOrcamentoRepository.findByOrcamentoId(orcamentoId);
         return itens.stream()
                     .mapToDouble(ItemOrcamento::getSubtotal)
                     .sum();
     }


    private ItemOrcamentoResponseDTO convertToDTO(ItemOrcamento itemOrcamento) {
        ItemOrcamentoResponseDTO dto = new ItemOrcamentoResponseDTO();
        dto.setId(itemOrcamento.getId());
        dto.setOrcamentoId(itemOrcamento.getOrcamento().getId());
        dto.setPecaMaterialId(itemOrcamento.getPecaMaterial() != null ? itemOrcamento.getPecaMaterial().getId() : null);
        dto.setCodigoPecaMaterial(itemOrcamento.getPecaMaterial() != null ? itemOrcamento.getPecaMaterial().getCodigo() : null);
        dto.setDescricaoPecaMaterial(itemOrcamento.getPecaMaterial() != null ? itemOrcamento.getPecaMaterial().getDescricao() : null);
        dto.setTipoServicoId(itemOrcamento.getTipoServico() != null ? itemOrcamento.getTipoServico().getId() : null);
        dto.setDescricaoTipoServico(itemOrcamento.getTipoServico() != null ? itemOrcamento.getTipoServico().getDescricao() : null);
        dto.setDescricao(itemOrcamento.getDescricao());
        dto.setQuantidade(itemOrcamento.getQuantidade());
        dto.setValorUnitario(itemOrcamento.getValorUnitario());
        dto.setSubtotal(itemOrcamento.getSubtotal());
        return dto;
    }

    // Método para converter DTO para Entidade
    private ItemOrcamento convertToEntity(ItemOrcamentoRequestDTO itemOrcamentoRequestDTO) {
        ItemOrcamento item = new ItemOrcamento();
        // orcamento, pecaMaterial, tipoServico definidos no service
        item.setDescricao(itemOrcamentoRequestDTO.getDescricao());
        item.setQuantidade(itemOrcamentoRequestDTO.getQuantidade());
        item.setValorUnitario(itemOrcamentoRequestDTO.getValorUnitario());
        // subtotal calculado no service
        return item;
    }
}