package com.codagis.nordeste_servicos.util;

import com.codagis.nordeste_servicos.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    public static final String CLAIM_USER_ID = "uid";
    public static final String CLAIM_PERFIL = "perfil";

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.access-token-validity-ms:900000}")
    private long accessTokenValidityMs;

    private Key secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("jwt.secret must be at least 32 bytes (256 bits) in UTF-8");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private void ensureKey() {
        if (secretKey == null) {
            init();
        }
    }

    public String generateAccessToken(Usuario usuario) {
        ensureKey();
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER_ID, usuario.getId());
        claims.put(CLAIM_PERFIL, usuario.getPerfil().name());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidityMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object raw = claims.get(CLAIM_USER_ID);
        if (raw instanceof Number) {
            return ((Number) raw).longValue();
        }
        if (raw instanceof String) {
            return Long.parseLong((String) raw);
        }
        return null;
    }

    public String extractPerfil(String token) {
        Claims claims = extractAllClaims(token);
        Object raw = claims.get(CLAIM_PERFIL);
        return raw != null ? raw.toString() : null;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        ensureKey();
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isAccessTokenValid(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token, String username) {
        try {
            final String subject = extractUsername(token);
            return subject != null && subject.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
