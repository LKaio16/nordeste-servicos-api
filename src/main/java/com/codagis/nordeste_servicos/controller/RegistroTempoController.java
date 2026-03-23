package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.RegistroTempoRequestDTO;
import com.codagis.nordeste_servicos.dto.RegistroTempoResponseDTO;
import com.codagis.nordeste_servicos.service.RegistroTempoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ordens-servico/{osId}/registros-tempo")
public class RegistroTempoController {

    @Autowired
    private RegistroTempoService registroTempoService;

    @GetMapping
    public ResponseEntity<List<RegistroTempoResponseDTO>> getRegistrosTempoByOrdemServico(@PathVariable Long osId) {
        List<RegistroTempoResponseDTO> registros = registroTempoService.findRegistrosTempoByOrdemServicoId(osId);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroTempoResponseDTO> getRegistroTempoById(@PathVariable Long osId, @PathVariable Long id) {
        RegistroTempoResponseDTO registro = registroTempoService.findRegistroTempoById(id);
        return ResponseEntity.ok(registro);
    }

    @PostMapping("/iniciar")
    public ResponseEntity<RegistroTempoResponseDTO> iniciarRegistroTempo(@PathVariable Long osId, @RequestBody RegistroTempoRequestDTO registroTempoRequestDTO) {
        registroTempoRequestDTO.setOrdemServicoId(osId);

        RegistroTempoResponseDTO novoRegistro = registroTempoService.iniciarRegistroTempo(registroTempoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoRegistro);
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<RegistroTempoResponseDTO> finalizarRegistroTempo(
            @PathVariable Long osId,
            @PathVariable Long id,
            @RequestBody(required = false) RegistroTempoRequestDTO registroTempoRequestDTO
    ) {
        // Para "edição" no web: se vier horaTermino no body, atualizamos.
        // Se não vier, cai no comportamento padrão (finaliza agora).
        LocalDateTime horaTermino = registroTempoRequestDTO != null ? registroTempoRequestDTO.getHoraTermino() : null;
        RegistroTempoResponseDTO registroAtualizado = registroTempoService.finalizarRegistroTempo(id, horaTermino);
        return ResponseEntity.ok(registroAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistroTempo(@PathVariable Long osId, @PathVariable Long id) {
        registroTempoService.deleteRegistroTempo(id);
        return ResponseEntity.noContent().build();
    }
}