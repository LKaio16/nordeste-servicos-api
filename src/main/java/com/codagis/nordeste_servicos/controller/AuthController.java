// src/main/java/com/codagis/nordeste_servicos/controller/AuthController.java
package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.LoginRequestDTO;
import com.codagis.nordeste_servicos.dto.LoginResponseDTO; // Importe o novo DTO de resposta
import com.codagis.nordeste_servicos.model.Usuario;
import com.codagis.nordeste_servicos.repository.UsuarioRepository;
import com.codagis.nordeste_servicos.service.UsuarioService;
import com.codagis.nordeste_servicos.util.JwtUtil; // Importe o JwtUtil
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
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil; // Injete o JwtUtil

    // O UsuarioService pode não ser mais necessário aqui, se você estiver retornando um DTO simples
    // @Autowired
    // private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(loginRequestDTO.getEmail());

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            if (passwordEncoder.matches(loginRequestDTO.getSenha(), usuario.getSenha())) {
                // Autenticação bem-sucedida
                // Gerar o token JWT
                String token = jwtUtil.generateToken(usuario.getEmail()); // Use o email como subject

                // Criar o DTO de resposta com os dados do usuário e o token
                LoginResponseDTO responseDTO = new LoginResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getCracha(),
                        usuario.getEmail(),
                        usuario.getPerfil(),
                        token // Inclua o token aqui
                );
                return ResponseEntity.ok(responseDTO);
            } else {
                // Senha incorreta
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
            }
        } else {
            // Usuário não encontrado
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        }
    }
}