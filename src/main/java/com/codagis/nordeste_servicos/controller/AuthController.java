// src/main/java/com/codagis/nordeste_servicos/controller/AuthController.java
package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.LoginRequestDTO;
import com.codagis.nordeste_servicos.dto.LoginResponseDTO;
import com.codagis.nordeste_servicos.model.Usuario;
import com.codagis.nordeste_servicos.service.UsuarioService; // Alterado
import com.codagis.nordeste_servicos.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService; // <-- ALTERADO para injetar o serviço

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        // Agora chamamos o serviço, que é transacional
        Optional<Usuario> usuarioOptional = usuarioService.findByEmail(loginRequestDTO.getEmail());

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            if (passwordEncoder.matches(loginRequestDTO.getSenha(), usuario.getSenha())) {
                String token = jwtUtil.generateToken(usuario.getEmail());

                LoginResponseDTO responseDTO = new LoginResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getCracha(),
                        usuario.getEmail(),
                        usuario.getPerfil(),
                        token,
                        usuario.getFotoPerfil() // Incluindo a foto no DTO de login
                );
                return ResponseEntity.ok(responseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("message", "Email ou senha inválidos."));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("message", "Email ou senha inválidos."));
        }
    }
}