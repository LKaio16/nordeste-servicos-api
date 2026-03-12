package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.FornecedorRequestDTO;
import com.codagis.nordeste_servicos.dto.FornecedorResponseDTO;
import com.codagis.nordeste_servicos.service.FornecedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {

    @Autowired
    private FornecedorService fornecedorService;

    @GetMapping
    public ResponseEntity<List<FornecedorResponseDTO>> getAll(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(fornecedorService.findAll(searchTerm, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(fornecedorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<FornecedorResponseDTO> create(@Valid @RequestBody FornecedorRequestDTO dto) {
        FornecedorResponseDTO saved = fornecedorService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> update(@PathVariable Long id, @Valid @RequestBody FornecedorRequestDTO dto) {
        return ResponseEntity.ok(fornecedorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fornecedorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
