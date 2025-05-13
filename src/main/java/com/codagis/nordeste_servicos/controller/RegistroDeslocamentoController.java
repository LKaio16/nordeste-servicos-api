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
          // TODO: Opcional: Adicionar verificação se registro.getOrdemServicoId() == osId
         return ResponseEntity.ok(registro);
     }


    @PostMapping
    public ResponseEntity<RegistroDeslocamentoResponseDTO> createRegistroDeslocamento(@PathVariable Long osId, @RequestBody RegistroDeslocamentoRequestDTO registroDeslocamentoRequestDTO) {
        // TODO: Validar que registroDeslocamentoRequestDTO.getOrdemServicoId() == osId
        // TODO: Obter o ID do técnico logado da sessão/contexto de segurança
        // registroDeslocamentoRequestDTO.setTecnicoId(idDoTecnicoLogado);
         registroDeslocamentoRequestDTO.setOrdemServicoId(osId); // Garante que a OS da URL é usada

        RegistroDeslocamentoResponseDTO novoRegistro = registroDeslocamentoService.createRegistroDeslocamento(registroDeslocamentoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoRegistro);
    }

     @PutMapping("/{id}")
     public ResponseEntity<RegistroDeslocamentoResponseDTO> updateRegistroDeslocamento(@PathVariable Long osId, @PathVariable Long id, @RequestBody RegistroDeslocamentoRequestDTO registroDeslocamentoRequestDTO) {
         // TODO: Opcional: Adicionar verificação se o registro de deslocamento pertence à OS osId
         // TODO: Validar que registroDeslocamentoRequestDTO.getOrdemServicoId() == osId (se enviado no body)
         // TODO: Obter o ID do técnico logado para validação de permissão
         RegistroDeslocamentoResponseDTO updatedRegistro = registroDeslocamentoService.updateRegistroDeslocamento(id, registroDeslocamentoRequestDTO);
         return ResponseEntity.ok(updatedRegistro);
     }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistroDeslocamento(@PathVariable Long osId, @PathVariable Long id) {
        // TODO: Opcional: Adicionar verificação se o registro de deslocamento pertence à OS osId
        // TODO: Validar permissão para deletar
        registroDeslocamentoService.deleteRegistroDeslocamento(id);
        return ResponseEntity.noContent().build();
    }
}