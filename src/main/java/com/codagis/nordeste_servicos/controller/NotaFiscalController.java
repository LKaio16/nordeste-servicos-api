package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.NotaFiscalRequestDTO;
import com.codagis.nordeste_servicos.dto.NotaFiscalResponseDTO;
import com.codagis.nordeste_servicos.service.NotaFiscalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notas-fiscais")
public class NotaFiscalController {

    @Autowired
    private NotaFiscalService notaFiscalService;

    @GetMapping
    public ResponseEntity<List<NotaFiscalResponseDTO>> getAll(
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String tipo) {
        return ResponseEntity.ok(notaFiscalService.findAll(fornecedorId, clienteId, tipo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotaFiscalResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(notaFiscalService.findById(id));
    }

    @PostMapping
    public ResponseEntity<NotaFiscalResponseDTO> create(@Valid @RequestBody NotaFiscalRequestDTO dto) {
        NotaFiscalResponseDTO saved = notaFiscalService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotaFiscalResponseDTO> update(@PathVariable Long id, @Valid @RequestBody NotaFiscalRequestDTO dto) {
        return ResponseEntity.ok(notaFiscalService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notaFiscalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
