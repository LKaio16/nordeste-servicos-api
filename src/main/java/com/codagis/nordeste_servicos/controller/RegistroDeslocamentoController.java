package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.RegistroDeslocamentoRequestDTO;
import com.codagis.nordeste_servicos.dto.RegistroDeslocamentoResponseDTO;
import com.codagis.nordeste_servicos.service.RegistroDeslocamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordens-servico/{osId}/registros-deslocamento")
public class RegistroDeslocamentoController {

    @Autowired
    private RegistroDeslocamentoService registroDeslocamentoService;

    @GetMapping
    public ResponseEntity<List<RegistroDeslocamentoResponseDTO>> getRegistrosDeslocamentoByOrdemServico(@PathVariable Long osId) {
        List<RegistroDeslocamentoResponseDTO> registros = registroDeslocamentoService.findRegistrosDeslocamentoByOrdemServicoId(osId);
        return ResponseEntity.ok(registros);
    }

     @GetMapping("/{id}")
     public ResponseEntity<RegistroDeslocamentoResponseDTO> getRegistroDeslocamentoById(@PathVariable Long osId, @PathVariable Long id) {
         RegistroDeslocamentoResponseDTO registro = registroDeslocamentoService.findRegistroDeslocamentoById(id);

         return ResponseEntity.ok(registro);
     }

    @PostMapping
    public ResponseEntity<RegistroDeslocamentoResponseDTO> createRegistroDeslocamento(@PathVariable Long osId, @RequestBody RegistroDeslocamentoRequestDTO registroDeslocamentoRequestDTO) {

         registroDeslocamentoRequestDTO.setOrdemServicoId(osId);

        RegistroDeslocamentoResponseDTO novoRegistro = registroDeslocamentoService.createRegistroDeslocamento(registroDeslocamentoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoRegistro);
    }

     @PutMapping("/{id}")
     public ResponseEntity<RegistroDeslocamentoResponseDTO> updateRegistroDeslocamento(@PathVariable Long osId, @PathVariable Long id, @RequestBody RegistroDeslocamentoRequestDTO registroDeslocamentoRequestDTO) {

         RegistroDeslocamentoResponseDTO updatedRegistro = registroDeslocamentoService.updateRegistroDeslocamento(id, registroDeslocamentoRequestDTO);
         return ResponseEntity.ok(updatedRegistro);
     }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistroDeslocamento(@PathVariable Long osId, @PathVariable Long id) {

        registroDeslocamentoService.deleteRegistroDeslocamento(id);
        return ResponseEntity.noContent().build();
    }
}