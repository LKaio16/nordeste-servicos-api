package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.EquipamentoRequestDTO;
import com.codagis.nordeste_servicos.dto.EquipamentoResponseDTO;
import com.codagis.nordeste_servicos.service.EquipamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipamentos")
public class EquipamentoController {

    @Autowired
    private EquipamentoService equipamentoService;

    @GetMapping
    public ResponseEntity<List<EquipamentoResponseDTO>> getAllEquipamentos(@RequestParam(required = false) Long clienteId) {
        if (clienteId != null) {
            List<EquipamentoResponseDTO> equipamentos = equipamentoService.findEquipamentosByClienteId(clienteId);
            return ResponseEntity.ok(equipamentos);
        }
        List<EquipamentoResponseDTO> equipamentos = equipamentoService.findAllEquipamentos();
        return ResponseEntity.ok(equipamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipamentoResponseDTO> getEquipamentoById(@PathVariable Long id) {
        EquipamentoResponseDTO equipamento = equipamentoService.findEquipamentoById(id);
        return ResponseEntity.ok(equipamento);
    }

    @PostMapping
    public ResponseEntity<EquipamentoResponseDTO> createEquipamento(@RequestBody EquipamentoRequestDTO equipamentoRequestDTO) {
        EquipamentoResponseDTO savedEquipamento = equipamentoService.createEquipamento(equipamentoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEquipamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipamentoResponseDTO> updateEquipamento(@PathVariable Long id, @RequestBody EquipamentoRequestDTO equipamentoRequestDTO) {
        EquipamentoResponseDTO updatedEquipamento = equipamentoService.updateEquipamento(id, equipamentoRequestDTO);
        return ResponseEntity.ok(updatedEquipamento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipamento(@PathVariable Long id) {
        equipamentoService.deleteEquipamento(id);
        return ResponseEntity.noContent().build();
    }
}