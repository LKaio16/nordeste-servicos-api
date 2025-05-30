// src/main/java/com/codagis/nordeste_servicos/util/JwtUtil.java
package com.codagis.nordeste_servicos.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // SECRET_KEY deve ser uma string forte e secreta.
    // Você pode injetá-la de application.properties ou variáveis de ambiente.
    @Value("${jwt.secret:UmaChaveSecretaMuitoForteParaAssinarSeusTokensJWTQueNinguemVaiAdivinharComPeloMenos256Bits}")
    private String SECRET_KEY_STRING; // Vai ler do application.properties
    private Key SECRET_KEY; // A chave real em formato Key

    // Inicializa a chave secreta a partir da string
    // Este método será chamado automaticamente pelo Spring após a injeção de valor.
    public void init() {
        SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());
    }

    // Tempo de expiração do token (por exemplo, 10 horas em milissegundos)
    public static final long JWT_TOKEN_VALIDITY = 10 * 60 * 60 * 1000; // 10 horas

    // Recupera o nome de usuário do token JWT
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Recupera a data de expiração do token JWT
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Recupera um claim específico do token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Para recuperar qualquer informação do token, precisaremos da chave secreta
    private Claims extractAllClaims(String token) {
        if (SECRET_KEY == null) {
            init(); // Garante que a chave foi inicializada
        }
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    // Verifica se o token expirou
    private Boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    // Gera o token para um usuário
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // Cria o token
    private String createToken(Map<String, Object> claims, String subject) {
        if (SECRET_KEY == null) {
            init(); // Garante que a chave foi inicializada
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // Valida o token
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}