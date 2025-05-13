package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.ItemOSUtilizadoRequestDTO;
import com.codagis.nordeste_servicos.dto.ItemOSUtilizadoResponseDTO;
import com.codagis.nordeste_servicos.service.ItemOSUtilizadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordens-servico/{osId}/itens-utilizados")
public class ItemOSUtilizadoController {

    @Autowired
    private ItemOSUtilizadoService itemOSUtilizadoService;

    @GetMapping
    public ResponseEntity<List<ItemOSUtilizadoResponseDTO>> getItensUtilizadosByOrdemServico(@PathVariable Long osId) {
        List<ItemOSUtilizadoResponseDTO> itens = itemOSUtilizadoService.findItensUtilizadosByOrdemServicoId(osId);
        return ResponseEntity.ok(itens);
    }

     @GetMapping("/{id}")
     public ResponseEntity<ItemOSUtilizadoResponseDTO> getItemOSUtilizadoById(@PathVariable Long osId, @PathVariable Long id) {
         ItemOSUtilizadoResponseDTO item = itemOSUtilizadoService.findItemOSUtilizadoById(id);
          // TODO: Opcional: Adicionar verificação se item.getOrdemServicoId() == osId
         return ResponseEntity.ok(item);
     }


    @PostMapping
    public ResponseEntity<ItemOSUtilizadoResponseDTO> createItemOSUtilizado(@PathVariable Long osId, @RequestBody ItemOSUtilizadoRequestDTO itemOSUtilizadoRequestDTO) {
        // TODO: Validar que itemOSUtilizadoRequestDTO.getOrdemServicoId() == osId
        // TODO: Validar permissão (quem pode adicionar itens a uma OS?)
         itemOSUtilizadoRequestDTO.setOrdemServicoId(osId); // Garante que a OS da URL é usada

        ItemOSUtilizadoResponseDTO novoItem = itemOSUtilizadoService.createItemOSUtilizado(itemOSUtilizadoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
    }

     @PutMapping("/{id}")
     public ResponseEntity<ItemOSUtilizadoResponseDTO> updateItemOSUtilizado(@PathVariable Long osId, @PathVariable Long id, @RequestBody ItemOSUtilizadoRequestDTO itemOSUtilizadoRequestDTO) {
         // TODO: Opcional: Adicionar verificação se o item pertence à OS osId
         // TODO: Validar que itemOSUtilizadoRequestDTO.getOrdemServicoId() == osId (se enviado no body)
         // TODO: Validar permissão para atualizar
         ItemOSUtilizadoResponseDTO updatedItem = itemOSUtilizadoService.updateItemOSUtilizado(id, itemOSUtilizadoRequestDTO);
         return ResponseEntity.ok(updatedItem);
     }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemOSUtilizado(@PathVariable Long osId, @PathVariable Long id) {
        // TODO: Opcional: Adicionar verificação se o item pertence à OS osId
        // TODO: Validar permissão para deletar
        itemOSUtilizadoService.deleteItemOSUtilizado(id);
        return ResponseEntity.noContent().build();
    }
}