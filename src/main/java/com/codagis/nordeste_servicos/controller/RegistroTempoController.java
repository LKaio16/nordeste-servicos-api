package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.RegistroTempoRequestDTO;
import com.codagis.nordeste_servicos.dto.RegistroTempoResponseDTO;
import com.codagis.nordeste_servicos.service.RegistroTempoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Endpoint para obter um registro de tempo específico
    @GetMapping("/{id}")
     public ResponseEntity<RegistroTempoResponseDTO> getRegistroTempoById(@PathVariable Long osId, @PathVariable Long id) {
         // Embora o service encontre por ID global, o aninhamento no controller reforça a relação
         RegistroTempoResponseDTO registro = registroTempoService.findRegistroTempoById(id);
         // Opcional: Adicionar verificação se registro.getOrdemServicoId() == osId para garantir consistência na URL
         return ResponseEntity.ok(registro);
     }


    // Endpoint para INICIAR um novo registro de tempo (timer)
    @PostMapping("/iniciar")
    public ResponseEntity<RegistroTempoResponseDTO> iniciarRegistroTempo(@PathVariable Long osId, @RequestBody RegistroTempoRequestDTO registroTempoRequestDTO) {
        // TODO: Validar que registroTempoRequestDTO.getOrdemServicoId() == osId
        // TODO: Obter o ID do técnico logado da sessão/contexto de segurança
        //  registroTempoRequestDTO.setTecnicoId(idDoTecnicoLogado);
        registroTempoRequestDTO.setOrdemServicoId(osId); // Garante que a OS da URL é usada

        RegistroTempoResponseDTO novoRegistro = registroTempoService.iniciarRegistroTempo(registroTempoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoRegistro);
    }

    // Endpoint para FINALIZAR um registro de tempo (parar/pausar timer)
    // O ID no path é o ID do RegistroTempo a ser finalizado
    @PutMapping("/{id}/finalizar")
     public ResponseEntity<RegistroTempoResponseDTO> finalizarRegistroTempo(@PathVariable Long osId, @PathVariable Long id) {
         // TODO: Opcional: Adicionar verificação se o registro de tempo pertence à OS osId e ao técnico logado
         RegistroTempoResponseDTO registroFinalizado = registroTempoService.finalizarRegistroTempo(id);
         return ResponseEntity.ok(registroFinalizado);
     }


    // Endpoint para deletar um registro de tempo específico
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistroTempo(@PathVariable Long osId, @PathVariable Long id) {
        // TODO: Opcional: Adicionar verificação se o registro de tempo pertence à OS osId
        registroTempoService.deleteRegistroTempo(id);
        return ResponseEntity.noContent().build();
    }
}