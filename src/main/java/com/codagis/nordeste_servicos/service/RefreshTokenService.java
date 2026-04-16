package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.model.RefreshToken;
import com.codagis.nordeste_servicos.model.Usuario;
import com.codagis.nordeste_servicos.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${jwt.refresh-token-validity-ms:1209600000}")
    private long refreshTokenValidityMs;

    private static String sha256Hex(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    @Transactional
    public String createRefreshToken(Usuario usuario) {
        byte[] raw = new byte[32];
        secureRandom.nextBytes(raw);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
        String hash = sha256Hex(token);

        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hash)
                .userId(usuario.getId())
                .expiresAt(Instant.now().plusMillis(refreshTokenValidityMs))
                .revoked(false)
                .build();
        refreshTokenRepository.save(entity);
        return token;
    }

    @Transactional(readOnly = true)
    public Optional<Long> validateAndGetUserId(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return Optional.empty();
        }
        String hash = sha256Hex(rawToken.trim());
        return refreshTokenRepository.findByTokenHashAndRevokedIsFalse(hash)
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()))
                .map(RefreshToken::getUserId);
    }

    @Transactional
    public Optional<String> rotateRefreshToken(String oldRawToken, Usuario usuario) {
        if (oldRawToken == null || oldRawToken.isBlank()) {
            return Optional.empty();
        }
        String hash = sha256Hex(oldRawToken.trim());
        Optional<RefreshToken> existing = refreshTokenRepository.findByTokenHashAndRevokedIsFalse(hash)
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()))
                .filter(rt -> rt.getUserId().equals(usuario.getId()));
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        refreshTokenRepository.revokeByHash(hash);
        return Optional.of(createRefreshToken(usuario));
    }

    @Transactional
    public void revokeByRawToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }
        refreshTokenRepository.revokeByHash(sha256Hex(rawToken.trim()));
    }

    @Transactional
    public void revokeAllForUser(Long userId) {
        refreshTokenRepository.revokeAllForUser(userId);
    }
}
