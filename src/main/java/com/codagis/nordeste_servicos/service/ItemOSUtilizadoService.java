package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.ItemOSUtilizadoRequestDTO;
import com.codagis.nordeste_servicos.dto.ItemOSUtilizadoResponseDTO;
import com.codagis.nordeste_servicos.exception.BusinessException;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.ItemOSUtilizado;
import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.model.PecaMaterial;
import com.codagis.nordeste_servicos.repository.ItemOSUtilizadoRepository;
import com.codagis.nordeste_servicos.repository.OrdemServicoRepository;
import com.codagis.nordeste_servicos.repository.PecaMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemOSUtilizadoService {

    @Autowired
    private ItemOSUtilizadoRepository itemOSUtilizadoRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private PecaMaterialRepository pecaMaterialRepository;

    public List<ItemOSUtilizadoResponseDTO> findItensUtilizadosByOrdemServicoId(Long ordemServicoId) {
        List<ItemOSUtilizado> itens = itemOSUtilizadoRepository.findByOrdemServicoId(ordemServicoId);
        return itens.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
    }

     public ItemOSUtilizadoResponseDTO findItemOSUtilizadoById(Long id) {
         ItemOSUtilizado item = itemOSUtilizadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item utilizado na OS não encontrado com ID: " + id));
         return convertToDTO(item);
     }

    public ItemOSUtilizadoResponseDTO createItemOSUtilizado(ItemOSUtilizadoRequestDTO itemOSUtilizadoRequestDTO) {
        OrdemServico ordemServico = ordemServicoRepository.findById(itemOSUtilizadoRequestDTO.getOrdemServicoId())
                 .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + itemOSUtilizadoRequestDTO.getOrdemServicoId()));

        PecaMaterial pecaMaterial = pecaMaterialRepository.findById(itemOSUtilizadoRequestDTO.getPecaMaterialId())
                 .orElseThrow(() -> new ResourceNotFoundException("Peça/Material não encontrado com ID: " + itemOSUtilizadoRequestDTO.getPecaMaterialId()));

        ItemOSUtilizado novoItem = convertToEntity(itemOSUtilizadoRequestDTO);
        novoItem.setOrdemServico(ordemServico);
        novoItem.setPecaMaterial(pecaMaterial);

        ItemOSUtilizado savedItem = itemOSUtilizadoRepository.save(novoItem);
        return convertToDTO(savedItem);
    }

    public ItemOSUtilizadoResponseDTO updateItemOSUtilizado(Long id, ItemOSUtilizadoRequestDTO itemOSUtilizadoRequestDTO) {
         ItemOSUtilizado existingItem = itemOSUtilizadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item utilizado na OS não encontrado com ID: " + id));

         OrdemServico ordemServico = ordemServicoRepository.findById(itemOSUtilizadoRequestDTO.getOrdemServicoId())
                 .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + itemOSUtilizadoRequestDTO.getOrdemServicoId()));

        PecaMaterial pecaMaterial = pecaMaterialRepository.findById(itemOSUtilizadoRequestDTO.getPecaMaterialId())
                 .orElseThrow(() -> new ResourceNotFoundException("Peça/Material não encontrado com ID: " + itemOSUtilizadoRequestDTO.getPecaMaterialId()));

         existingItem.setOrdemServico(ordemServico);
         existingItem.setPecaMaterial(pecaMaterial);
         existingItem.setQuantidadeRequisitada(itemOSUtilizadoRequestDTO.getQuantidadeRequisitada());
         existingItem.setQuantidadeUtilizada(itemOSUtilizadoRequestDTO.getQuantidadeUtilizada());
         existingItem.setQuantidadeDevolvida(itemOSUtilizadoRequestDTO.getQuantidadeDevolvida());

         ItemOSUtilizado updatedItem = itemOSUtilizadoRepository.save(existingItem);
         return convertToDTO(updatedItem);
    }

    public void deleteItemOSUtilizado(Long id) {
        if (!itemOSUtilizadoRepository.existsById(id)) {
             throw new ResourceNotFoundException("Item utilizado na OS não encontrado com ID: " + id);
        }

        itemOSUtilizadoRepository.deleteById(id);
    }

    private ItemOSUtilizadoResponseDTO convertToDTO(ItemOSUtilizado itemOSUtilizado) {
        ItemOSUtilizadoResponseDTO dto = new ItemOSUtilizadoResponseDTO();
        dto.setId(itemOSUtilizado.getId());
        dto.setOrdemServicoId(itemOSUtilizado.getOrdemServico().getId());
        dto.setPecaMaterialId(itemOSUtilizado.getPecaMaterial().getId());
        dto.setCodigoPecaMaterial(itemOSUtilizado.getPecaMaterial().getCodigo());
        dto.setDescricaoPecaMaterial(itemOSUtilizado.getPecaMaterial().getDescricao());
        dto.setPrecoUnitarioPecaMaterial(itemOSUtilizado.getPecaMaterial().getPreco());
        dto.setQuantidadeRequisitada(itemOSUtilizado.getQuantidadeRequisitada());
        dto.setQuantidadeUtilizada(itemOSUtilizado.getQuantidadeUtilizada());
        dto.setQuantidadeDevolvida(itemOSUtilizado.getQuantidadeDevolvida());

        return dto;
    }

    private ItemOSUtilizado convertToEntity(ItemOSUtilizadoRequestDTO itemOSUtilizadoRequestDTO) {
        ItemOSUtilizado item = new ItemOSUtilizado();
        item.setQuantidadeRequisitada(itemOSUtilizadoRequestDTO.getQuantidadeRequisitada());
        item.setQuantidadeUtilizada(itemOSUtilizadoRequestDTO.getQuantidadeUtilizada());
        item.setQuantidadeDevolvida(itemOSUtilizadoRequestDTO.getQuantidadeDevolvida());

        return item;
    }
}