package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.GerarParcelasRequestDTO;
import com.codagis.nordeste_servicos.dto.ParcelaRequestDTO;
import com.codagis.nordeste_servicos.dto.ParcelaResponseDTO;
import com.codagis.nordeste_servicos.service.ParcelaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contas/{contaId}/parcelas")
public class ParcelaController {

    @Autowired
    private ParcelaService parcelaService;

    @GetMapping
    public ResponseEntity<List<ParcelaResponseDTO>> listByConta(@PathVariable Long contaId) {
        return ResponseEntity.ok(parcelaService.findByContaId(contaId));
    }

    @PostMapping
    public ResponseEntity<ParcelaResponseDTO> create(
            @PathVariable Long contaId,
            @Valid @RequestBody ParcelaRequestDTO dto) {
        ParcelaResponseDTO saved = parcelaService.create(contaId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/gerar")
    public ResponseEntity<List<ParcelaResponseDTO>> gerarParcelas(
            @PathVariable Long contaId,
            @Valid @RequestBody GerarParcelasRequestDTO dto) {
        List<ParcelaResponseDTO> list = parcelaService.gerarParcelas(contaId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(list);
    }

    @PutMapping("/{parcelaId}")
    public ResponseEntity<ParcelaResponseDTO> update(
            @PathVariable Long contaId,
            @PathVariable Long parcelaId,
            @Valid @RequestBody ParcelaRequestDTO dto) {
        return ResponseEntity.ok(parcelaService.update(contaId, parcelaId, dto));
    }

    @PutMapping("/{parcelaId}/pagar")
    public ResponseEntity<ParcelaResponseDTO> marcarComoPaga(
            @PathVariable Long contaId,
            @PathVariable Long parcelaId,
            @RequestBody(required = false) Map<String, Object> body) {
        LocalDate dataPagamento = body != null && body.get("dataPagamento") != null
                ? LocalDate.parse(body.get("dataPagamento").toString())
                : LocalDate.now();
        return ResponseEntity.ok(parcelaService.marcarComoPaga(contaId, parcelaId, dataPagamento));
    }

    @DeleteMapping("/{parcelaId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long contaId,
            @PathVariable Long parcelaId) {
        parcelaService.delete(contaId, parcelaId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/todas")
    public ResponseEntity<Void> deleteAll(@PathVariable Long contaId) {
        parcelaService.deleteAllByContaId(contaId);
        return ResponseEntity.noContent().build();
    }
}
