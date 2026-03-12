package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.ContaRequestDTO;
import com.codagis.nordeste_servicos.dto.ContaResponseDTO;
import com.codagis.nordeste_servicos.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @GetMapping
    public ResponseEntity<List<ContaResponseDTO>> getAll(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(contaService.findAll(clienteId, fornecedorId, tipo, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ContaResponseDTO> create(@Valid @RequestBody ContaRequestDTO dto) {
        ContaResponseDTO saved = contaService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ContaRequestDTO dto) {
        return ResponseEntity.ok(contaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<ContaResponseDTO> marcarComoPaga(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> body) {
        LocalDate dataPagamento = body != null && body.get("dataPagamento") != null
                ? LocalDate.parse(body.get("dataPagamento").toString())
                : LocalDate.now();
        String formaPagamento = body != null && body.get("formaPagamento") != null
                ? body.get("formaPagamento").toString() : null;
        return ResponseEntity.ok(contaService.marcarComoPaga(id, dataPagamento, formaPagamento));
    }
}
