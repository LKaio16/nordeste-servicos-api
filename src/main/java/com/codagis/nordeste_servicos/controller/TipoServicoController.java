package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.TipoServicoRequestDTO;
import com.codagis.nordeste_servicos.dto.TipoServicoResponseDTO;
import com.codagis.nordeste_servicos.service.TipoServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-servico")
public class TipoServicoController {

    @Autowired
    private TipoServicoService tipoServicoService;

    @GetMapping
    public ResponseEntity<List<TipoServicoResponseDTO>> getAllTiposServico() {
        List<TipoServicoResponseDTO> tiposServico = tipoServicoService.findAllTiposServico();
        return ResponseEntity.ok(tiposServico);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoServicoResponseDTO> getTipoServicoById(@PathVariable Long id) {
        TipoServicoResponseDTO tipoServico = tipoServicoService.findTipoServicoById(id);
        return ResponseEntity.ok(tipoServico);
    }

    @PostMapping
    public ResponseEntity<TipoServicoResponseDTO> createTipoServico(@RequestBody TipoServicoRequestDTO tipoServicoRequestDTO) {
        TipoServicoResponseDTO savedTipoServico = tipoServicoService.createTipoServico(tipoServicoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTipoServico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoServicoResponseDTO> updateTipoServico(@PathVariable Long id, @RequestBody TipoServicoRequestDTO tipoServicoRequestDTO) {
        TipoServicoResponseDTO updatedTipoServico = tipoServicoService.updateTipoServico(id, tipoServicoRequestDTO);
        return ResponseEntity.ok(updatedTipoServico);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipoServico(@PathVariable Long id) {
        tipoServicoService.deleteTipoServico(id);
        return ResponseEntity.noContent().build();
    }
}