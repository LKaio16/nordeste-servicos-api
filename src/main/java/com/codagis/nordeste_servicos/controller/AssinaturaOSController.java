package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.AssinaturaOSRequestDTO;
import com.codagis.nordeste_servicos.dto.AssinaturaOSResponseDTO;
import com.codagis.nordeste_servicos.service.AssinaturaOSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/ordens-servico/{osId}/assinatura")
public class AssinaturaOSController {

    @Autowired
    private AssinaturaOSService assinaturaOSService;

    @GetMapping
    public ResponseEntity<AssinaturaOSResponseDTO> getAssinaturaByOrdemServico(@PathVariable Long osId) {
        Optional<AssinaturaOSResponseDTO> assinatura = assinaturaOSService.findAssinaturaByOrdemServicoId(osId);
        return assinatura.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<AssinaturaOSResponseDTO> saveOrUpdateAssinatura(@PathVariable Long osId, @RequestBody AssinaturaOSRequestDTO requestDTO) {
        AssinaturaOSResponseDTO savedAssinatura = assinaturaOSService.saveOrUpdateAssinatura(osId, requestDTO);
        return ResponseEntity.ok(savedAssinatura);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAssinatura(@PathVariable Long osId) {
        assinaturaOSService.deleteAssinatura(osId);
        return ResponseEntity.noContent().build();
    }
}