package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.PecaMaterialRequestDTO;
import com.codagis.nordeste_servicos.dto.PecaMaterialResponseDTO;
import com.codagis.nordeste_servicos.service.PecaMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pecas-materiais")
public class PecaMaterialController {

    @Autowired
    private PecaMaterialService pecaMaterialService;

    @GetMapping
    public ResponseEntity<List<PecaMaterialResponseDTO>> getAllPecasMateriais() {
        List<PecaMaterialResponseDTO> pecasMateriais = pecaMaterialService.findAllPecasMateriais();
        return ResponseEntity.ok(pecasMateriais);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PecaMaterialResponseDTO> getPecaMaterialById(@PathVariable Long id) {
        PecaMaterialResponseDTO pecaMaterial = pecaMaterialService.findPecaMaterialById(id);
        return ResponseEntity.ok(pecaMaterial);
    }

    @PostMapping
    public ResponseEntity<PecaMaterialResponseDTO> createPecaMaterial(@RequestBody PecaMaterialRequestDTO pecaMaterialRequestDTO) {
        PecaMaterialResponseDTO savedPecaMaterial = pecaMaterialService.createPecaMaterial(pecaMaterialRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPecaMaterial);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PecaMaterialResponseDTO> updatePecaMaterial(@PathVariable Long id, @RequestBody PecaMaterialRequestDTO pecaMaterialRequestDTO) {
        PecaMaterialResponseDTO updatedPecaMaterial = pecaMaterialService.updatePecaMaterial(id, pecaMaterialRequestDTO);
        return ResponseEntity.ok(updatedPecaMaterial);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePecaMaterial(@PathVariable Long id) {
        pecaMaterialService.deletePecaMaterial(id);
        return ResponseEntity.noContent().build();
    }
}