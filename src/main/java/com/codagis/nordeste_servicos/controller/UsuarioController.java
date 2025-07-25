package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.DesempenhoTecnicoDTO;
import com.codagis.nordeste_servicos.dto.UsuarioRequestDTO;
import com.codagis.nordeste_servicos.dto.UsuarioResponseDTO;
import com.codagis.nordeste_servicos.dto.SenhaUpdateRequestDTO;
import com.codagis.nordeste_servicos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getAllUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.findAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.findUsuarioById(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> createUsuario(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO savedUsuario = usuarioService.createUsuario(usuarioRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUsuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> updateUsuario(@PathVariable Long id, @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO updatedUsuario = usuarioService.updateUsuario(id, usuarioRequestDTO);
        return ResponseEntity.ok(updatedUsuario);
    }

    @PatchMapping("/{id}/senha")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody SenhaUpdateRequestDTO request) {
        // TODO: Adicionar validação de segurança para garantir que apenas um ADMIN pode fazer isso.
        usuarioService.updatePassword(id, request.getNovaSenha());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/desempenho")
    public ResponseEntity<List<DesempenhoTecnicoDTO>> getDesempenhoDosTecnicos() {
        List<DesempenhoTecnicoDTO> desempenho = usuarioService.getDesempenhoTecnicos();
        return ResponseEntity.ok(desempenho);
    }
}