package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.LoginRequestDTO;
import com.codagis.nordeste_servicos.dto.LoginResponseDTO;
import com.codagis.nordeste_servicos.dto.RefreshTokenRequestDTO;
import com.codagis.nordeste_servicos.dto.TokenResponseDTO;
import com.codagis.nordeste_servicos.model.Usuario;
import com.codagis.nordeste_servicos.service.RefreshTokenService;
import com.codagis.nordeste_servicos.service.UsuarioService;
import com.codagis.nordeste_servicos.util.JwtUtil;
import com.codagis.nordeste_servicos.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        String email = ValidationUtils.normalizeEmail(
                loginRequestDTO.getEmail() != null ? loginRequestDTO.getEmail() : "");
        if (email.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email ou senha inválidos."));
        }

        Optional<Usuario> usuarioOptional = usuarioService.findByEmail(email);

        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email ou senha inválidos."));
        }

        Usuario usuario = usuarioOptional.get();
        if (!usuarioService.matchesPassword(usuario, loginRequestDTO.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email ou senha inválidos."));
        }

        String accessToken = jwtUtil.generateAccessToken(usuario);
        String refreshToken = refreshTokenService.createRefreshToken(usuario);

        LoginResponseDTO responseDTO = new LoginResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCracha(),
                usuario.getEmail(),
                usuario.getPerfil(),
                accessToken,
                refreshToken,
                usuario.getFotoPerfil(),
                usuario.getFotoUrl()
        );
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequestDTO body) {
        if (body == null || body.getRefreshToken() == null || body.getRefreshToken().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh token obrigatório."));
        }

        Optional<Long> userIdOpt = refreshTokenService.validateAndGetUserId(body.getRefreshToken());
        if (userIdOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh token inválido ou expirado."));
        }

        Optional<Usuario> usuarioOpt = usuarioService.findByIdEntity(userIdOpt.get());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Usuário não encontrado."));
        }

        Usuario usuario = usuarioOpt.get();
        Optional<String> newRefreshOpt = refreshTokenService.rotateRefreshToken(body.getRefreshToken(), usuario);
        if (newRefreshOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh token inválido ou expirado."));
        }

        String newAccess = jwtUtil.generateAccessToken(usuario);
        return ResponseEntity.ok(new TokenResponseDTO(newAccess, newRefreshOpt.get()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody(required = false) RefreshTokenRequestDTO body) {
        if (body != null && body.getRefreshToken() != null && !body.getRefreshToken().isBlank()) {
            refreshTokenService.revokeByRawToken(body.getRefreshToken());
        }
        return ResponseEntity.noContent().build();
    }
}
