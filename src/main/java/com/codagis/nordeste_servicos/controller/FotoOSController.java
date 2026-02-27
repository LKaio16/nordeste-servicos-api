package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.FotoOSResponseDTO;
import com.codagis.nordeste_servicos.dto.FotoOSUploadRequestDTO;
import com.codagis.nordeste_servicos.service.FotoOSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordens-servico/{osId}/fotos")
public class FotoOSController {

    @Autowired
    private FotoOSService fotoOSService;

    @GetMapping
    public ResponseEntity<List<FotoOSResponseDTO>> getFotosByOrdemServico(@PathVariable Long osId) {
        List<FotoOSResponseDTO> fotos = fotoOSService.findFotosByOrdemServicoId(osId);
        return ResponseEntity.ok(fotos);
    }

    @GetMapping("/{fotoId}")
    public ResponseEntity<FotoOSResponseDTO> getFotoById(@PathVariable Long osId, @PathVariable Long fotoId) {
        FotoOSResponseDTO foto = fotoOSService.findFotoById(fotoId);
        return ResponseEntity.ok(foto);
    }

    @PostMapping
    public ResponseEntity<FotoOSResponseDTO> uploadFoto(@PathVariable Long osId, @RequestBody FotoOSUploadRequestDTO requestDTO) {
        FotoOSResponseDTO savedFoto = fotoOSService.saveFoto(osId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFoto);
    }

    @DeleteMapping("/{fotoId}")
    public ResponseEntity<Void> deleteFoto(@PathVariable Long osId, @PathVariable Long fotoId) {
        // A verificação se a foto pertence à OS pode ser feita no serviço, se necessário.
        fotoOSService.deleteFoto(fotoId);
        return ResponseEntity.noContent().build();
    }
}