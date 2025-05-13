package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.ItemOrcamentoRequestDTO;
import com.codagis.nordeste_servicos.dto.ItemOrcamentoResponseDTO;
import com.codagis.nordeste_servicos.service.ItemOrcamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orcamentos/{orcamentoId}/itens") // Aninhado sob Orcamento
public class ItemOrcamentoController {

    @Autowired
    private ItemOrcamentoService itemOrcamentoService;

    // Endpoint para listar itens de um orçamento específico
    @GetMapping
    public ResponseEntity<List<ItemOrcamentoResponseDTO>> getItensByOrcamento(@PathVariable Long orcamentoId) {
        List<ItemOrcamentoResponseDTO> itens = itemOrcamentoService.findItensByOrcamentoId(orcamentoId);
        return ResponseEntity.ok(itens);
    }

     // Endpoint para obter um item específico pelo ID
     @GetMapping("/{id}")
     public ResponseEntity<ItemOrcamentoResponseDTO> getItemOrcamentoById(@PathVariable Long orcamentoId, @PathVariable Long id) {
         ItemOrcamentoResponseDTO item = itemOrcamentoService.findItemOrcamentoById(id);
          // TODO: Opcional: Adicionar verificação se item.getOrcamentoId() == orcamentoId para consistência na URL
         return ResponseEntity.ok(item);
     }


    // Endpoint para adicionar um novo item ao orçamento
    @PostMapping
    public ResponseEntity<ItemOrcamentoResponseDTO> createItemOrcamento(@PathVariable Long orcamentoId, @RequestBody ItemOrcamentoRequestDTO itemOrcamentoRequestDTO) {
        // TODO: Validar que itemOrcamentoRequestDTO.getOrcamentoId() == orcamentoId ou garantir que a URL prevaleça
        // TODO: Validar permissão para adicionar itens a este orçamento
         itemOrcamentoRequestDTO.setOrcamentoId(orcamentoId); // Garante que o Orçamento da URL é usado

        ItemOrcamentoResponseDTO novoItem = itemOrcamentoService.createItemOrcamento(itemOrcamentoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
    }

     // Endpoint para atualizar um item no orçamento
     @PutMapping("/{id}")
     public ResponseEntity<ItemOrcamentoResponseDTO> updateItemOrcamento(@PathVariable Long orcamentoId, @PathVariable Long id, @RequestBody ItemOrcamentoRequestDTO itemOrcamentoRequestDTO) {
         // TODO: Opcional: Adicionar verificação se o item pertence ao Orçamento orcamentoId para consistência na URL
         // TODO: Validar que itemOrcamentoRequestDTO.getOrcamentoId() == orcamentoId (se enviado no body) ou garantir que a URL prevaleça
         // TODO: Validar permissão para atualizar
          itemOrcamentoRequestDTO.setOrcamentoId(orcamentoId); // Garante que o Orçamento da URL é usado

         ItemOrcamentoResponseDTO updatedItem = itemOrcamentoService.updateItemOrcamento(id, itemOrcamentoRequestDTO);
         return ResponseEntity.ok(updatedItem);
     }


    // Endpoint para deletar um item do orçamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemOrcamento(@PathVariable Long orcamentoId, @PathVariable Long id) {
        // TODO: Opcional: Adicionar verificação se o item pertence ao Orçamento orcamentoId para consistência na URL
        // TODO: Validar permissão para deletar
        itemOrcamentoService.deleteItemOrcamento(id);
        return ResponseEntity.noContent().build();
    }
}