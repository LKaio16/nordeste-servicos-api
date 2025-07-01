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
import java.util.stream.Collectors;

@Service
public class ItemOrcamentoService {

    @Autowired
    private ItemOrcamentoRepository itemOrcamentoRepository;

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private PecaMaterialRepository pecaMaterialRepository;

    @Autowired
    private TipoServicoRepository tipoServicoRepository;

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

    @Transactional
    public ItemOrcamentoResponseDTO createItemOrcamento(ItemOrcamentoRequestDTO itemOrcamentoRequestDTO) {
        System.out.println("[LOG] 1. createItemOrcamento - Iniciando criação do item.");
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
        novoItem.setSubtotal(novoItem.getQuantidade() * novoItem.getValorUnitario());

        ItemOrcamento savedItem = itemOrcamentoRepository.saveAndFlush(novoItem);
        System.out.println("[LOG] 2. createItemOrcamento - Item salvo no banco com ID: " + savedItem.getId());

        recalcularESalvarTotalOrcamento(orcamento.getId());

        return convertToDTO(savedItem);
    }

    @Transactional
    public ItemOrcamentoResponseDTO updateItemOrcamento(Long id, ItemOrcamentoRequestDTO itemOrcamentoRequestDTO) {
        System.out.println("[LOG] 1. updateItemOrcamento - Iniciando atualização do item ID: " + id);
        ItemOrcamento existingItem = itemOrcamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do Orçamento não encontrado com ID: " + id));

        Long orcamentoOriginalId = existingItem.getOrcamento().getId();

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

        existingItem.setOrcamento(orcamento);
        existingItem.setPecaMaterial(pecaMaterial);
        existingItem.setTipoServico(tipoServico);
        existingItem.setDescricao(itemOrcamentoRequestDTO.getDescricao());
        existingItem.setQuantidade(itemOrcamentoRequestDTO.getQuantidade());
        existingItem.setValorUnitario(itemOrcamentoRequestDTO.getValorUnitario());
        existingItem.setSubtotal(existingItem.getQuantidade() * existingItem.getValorUnitario());

        ItemOrcamento updatedItem = itemOrcamentoRepository.saveAndFlush(existingItem);
        System.out.println("[LOG] 2. updateItemOrcamento - Item ID " + id + " atualizado.");

        recalcularESalvarTotalOrcamento(updatedItem.getOrcamento().getId());

        if (!orcamentoOriginalId.equals(updatedItem.getOrcamento().getId())) {
            recalcularESalvarTotalOrcamento(orcamentoOriginalId);
        }

        return convertToDTO(updatedItem);
    }

    @Transactional
    public void deleteItemOrcamento(Long id) {
        ItemOrcamento item = itemOrcamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do Orçamento não encontrado com ID: " + id));

        Long orcamentoIdAfetado = item.getOrcamento().getId();
        itemOrcamentoRepository.delete(item);

        recalcularESalvarTotalOrcamento(orcamentoIdAfetado);
    }

    private void recalcularESalvarTotalOrcamento(Long orcamentoId) {
        // Log inicial para garantir que o método foi chamado
        System.out.println("[LOG PASSO 1/5] Iniciando recalculo para Orçamento ID: " + orcamentoId);

        // Busca os itens do orçamento no banco de dados
        List<ItemOrcamento> itens = itemOrcamentoRepository.findByOrcamentoId(orcamentoId);
        System.out.println("[LOG PASSO 2/5] Encontrados " + itens.size() + " itens para o orçamento.");

        // Calcula o novo valor total somando os subtotais de cada item
        double total = 0.0;
        for(ItemOrcamento item : itens) {
            // Garante que subtotais nulos não quebrem o cálculo
            double subtotal = item.getSubtotal() != null ? item.getSubtotal() : 0.0;
            total += subtotal;
            System.out.println("    -> Somando subtotal do Item ID " + item.getId() + ": " + subtotal + " (Total parcial: " + total + ")");
        }
        System.out.println("[LOG PASSO 3/5] Valor total calculado: " + total);

        // Busca a entidade Orçamento que será atualizada
        Orcamento orcamentoParaAtualizar = orcamentoRepository.findById(orcamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Falha no recalculo: Orçamento com ID " + orcamentoId + " não encontrado."));

        // Atribui o novo valor
        orcamentoParaAtualizar.setValorTotal(total);

        // Salva e força a sincronização com o banco de dados (IMPORTANTE!)
        orcamentoRepository.saveAndFlush(orcamentoParaAtualizar);
        System.out.println("[LOG PASSO 4/5] Orçamento salvo no banco com o novo valor total.");

        // Opcional: Verifica o valor buscando o orçamento novamente para confirmar
        Orcamento orcamentoVerificacao = orcamentoRepository.findById(orcamentoId).get();
        System.out.println("[LOG PASSO 5/5] Verificação final. Valor no banco (pós-flush): " + orcamentoVerificacao.getValorTotal());
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

    private ItemOrcamento convertToEntity(ItemOrcamentoRequestDTO itemOrcamentoRequestDTO) {
        ItemOrcamento item = new ItemOrcamento();
        item.setDescricao(itemOrcamentoRequestDTO.getDescricao());
        item.setQuantidade(itemOrcamentoRequestDTO.getQuantidade());
        item.setValorUnitario(itemOrcamentoRequestDTO.getValorUnitario());
        return item;
    }
}