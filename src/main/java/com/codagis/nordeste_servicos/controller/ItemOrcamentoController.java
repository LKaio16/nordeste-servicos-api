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
@RequestMapping("/api/orcamentos/{orcamentoId}/itens")
public class ItemOrcamentoController {

    @Autowired
    private ItemOrcamentoService itemOrcamentoService;

    @GetMapping
    public ResponseEntity<List<ItemOrcamentoResponseDTO>> getItensByOrcamento(@PathVariable Long orcamentoId) {
        List<ItemOrcamentoResponseDTO> itens = itemOrcamentoService.findItensByOrcamentoId(orcamentoId);
        return ResponseEntity.ok(itens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemOrcamentoResponseDTO> getItemOrcamentoById(@PathVariable Long orcamentoId, @PathVariable Long id) {
        ItemOrcamentoResponseDTO item = itemOrcamentoService.findItemOrcamentoById(id);
        return ResponseEntity.ok(item);
    }

    @PostMapping
    public ResponseEntity<ItemOrcamentoResponseDTO> createItemOrcamento(@PathVariable Long orcamentoId, @RequestBody ItemOrcamentoRequestDTO itemOrcamentoRequestDTO) {
        itemOrcamentoRequestDTO.setOrcamentoId(orcamentoId);
        ItemOrcamentoResponseDTO novoItem = itemOrcamentoService.createItemOrcamento(itemOrcamentoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemOrcamentoResponseDTO> updateItemOrcamento(@PathVariable Long orcamentoId, @PathVariable Long id, @RequestBody ItemOrcamentoRequestDTO itemOrcamentoRequestDTO) {
        itemOrcamentoRequestDTO.setOrcamentoId(orcamentoId);
        ItemOrcamentoResponseDTO updatedItem = itemOrcamentoService.updateItemOrcamento(id, itemOrcamentoRequestDTO);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemOrcamento(@PathVariable Long orcamentoId, @PathVariable Long id) {
        itemOrcamentoService.deleteItemOrcamento(id);
        return ResponseEntity.noContent().build();
    }
}