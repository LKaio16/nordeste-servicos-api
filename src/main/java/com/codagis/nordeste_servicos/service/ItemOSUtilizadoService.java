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

    // Método para listar itens utilizados em uma OS
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

    // Método para adicionar um novo item utilizado na OS
    public ItemOSUtilizadoResponseDTO createItemOSUtilizado(ItemOSUtilizadoRequestDTO itemOSUtilizadoRequestDTO) {
        OrdemServico ordemServico = ordemServicoRepository.findById(itemOSUtilizadoRequestDTO.getOrdemServicoId())
                 .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + itemOSUtilizadoRequestDTO.getOrdemServicoId()));

        PecaMaterial pecaMaterial = pecaMaterialRepository.findById(itemOSUtilizadoRequestDTO.getPecaMaterialId())
                 .orElseThrow(() -> new ResourceNotFoundException("Peça/Material não encontrado com ID: " + itemOSUtilizadoRequestDTO.getPecaMaterialId()));

        // TODO: Adicionar validação de negócio (ex: quantidade utilizada não pode ser negativa)
        // TODO: Implementar lógica de baixa de estoque na PecaMaterial se o controle de estoque for ativado

        ItemOSUtilizado novoItem = convertToEntity(itemOSUtilizadoRequestDTO);
        novoItem.setOrdemServico(ordemServico);
        novoItem.setPecaMaterial(pecaMaterial);


        ItemOSUtilizado savedItem = itemOSUtilizadoRepository.save(novoItem);
        return convertToDTO(savedItem);
    }

     // Método para atualizar um item utilizado na OS
    public ItemOSUtilizadoResponseDTO updateItemOSUtilizado(Long id, ItemOSUtilizadoRequestDTO itemOSUtilizadoRequestDTO) {
         ItemOSUtilizado existingItem = itemOSUtilizadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item utilizado na OS não encontrado com ID: " + id));

         OrdemServico ordemServico = ordemServicoRepository.findById(itemOSUtilizadoRequestDTO.getOrdemServicoId())
                 .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + itemOSUtilizadoRequestDTO.getOrdemServicoId()));

        PecaMaterial pecaMaterial = pecaMaterialRepository.findById(itemOSUtilizadoRequestDTO.getPecaMaterialId())
                 .orElseThrow(() -> new ResourceNotFoundException("Peça/Material não encontrado com ID: " + itemOSUtilizadoRequestDTO.getPecaMaterialId()));

         // TODO: Adicionar validação de negócio (ex: quantidade utilizada não pode ser negativa)
         // TODO: Implementar lógica de ajuste de estoque (se mudou a quantidade utilizada)

         existingItem.setOrdemServico(ordemServico); // Atualiza a OS associada se o ID mudou no DTO
         existingItem.setPecaMaterial(pecaMaterial); // Atualiza a Peça/Material associada
         existingItem.setQuantidadeRequisitada(itemOSUtilizadoRequestDTO.getQuantidadeRequisitada());
         existingItem.setQuantidadeUtilizada(itemOSUtilizadoRequestDTO.getQuantidadeUtilizada());
         existingItem.setQuantidadeDevolvida(itemOSUtilizadoRequestDTO.getQuantidadeDevolvida());

         ItemOSUtilizado updatedItem = itemOSUtilizadoRepository.save(existingItem);
         return convertToDTO(updatedItem);
    }


    // Método para deletar um item utilizado na OS
    public void deleteItemOSUtilizado(Long id) {
        if (!itemOSUtilizadoRepository.existsById(id)) {
             throw new ResourceNotFoundException("Item utilizado na OS não encontrado com ID: " + id);
        }
         // TODO: Implementar lógica de retorno de estoque se o controle de estoque for ativado
        itemOSUtilizadoRepository.deleteById(id);
    }


    private ItemOSUtilizadoResponseDTO convertToDTO(ItemOSUtilizado itemOSUtilizado) {
        ItemOSUtilizadoResponseDTO dto = new ItemOSUtilizadoResponseDTO();
        dto.setId(itemOSUtilizado.getId());
        dto.setOrdemServicoId(itemOSUtilizado.getOrdemServico().getId());
        dto.setPecaMaterialId(itemOSUtilizado.getPecaMaterial().getId());
        dto.setCodigoPecaMaterial(itemOSUtilizado.getPecaMaterial().getCodigo()); // Popula código
        dto.setDescricaoPecaMaterial(itemOSUtilizado.getPecaMaterial().getDescricao()); // Popula descrição
        dto.setPrecoUnitarioPecaMaterial(itemOSUtilizado.getPecaMaterial().getPreco()); // Popula preço unitário
        dto.setQuantidadeRequisitada(itemOSUtilizado.getQuantidadeRequisitada());
        dto.setQuantidadeUtilizada(itemOSUtilizado.getQuantidadeUtilizada());
        dto.setQuantidadeDevolvida(itemOSUtilizado.getQuantidadeDevolvida());
        // TODO: Calcular e setar subtotal no DTO se necessário
        // dto.setSubtotal(itemOSUtilizado.getQuantidadeUtilizada() * itemOSUtilizado.getPecaMaterial().getPreco());
        return dto;
    }

    // Método para converter DTO para Entidade
    private ItemOSUtilizado convertToEntity(ItemOSUtilizadoRequestDTO itemOSUtilizadoRequestDTO) {
        ItemOSUtilizado item = new ItemOSUtilizado();
        item.setQuantidadeRequisitada(itemOSUtilizadoRequestDTO.getQuantidadeRequisitada());
        item.setQuantidadeUtilizada(itemOSUtilizadoRequestDTO.getQuantidadeUtilizada());
        item.setQuantidadeDevolvida(itemOSUtilizadoRequestDTO.getQuantidadeDevolvida());
        // Relacionamentos (OrdemServico, PecaMaterial) definidos no serviço
        return item;
    }
}