// src/main/java/com/codagis/nordeste_servicos/dto/LoginResponseDTO.java
package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.PerfilUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // Adicionado para facilitar a criação com todos os campos
public class LoginResponseDTO {
    private Long id;
    private String nome;
    private String cracha;
    private String email;
    private PerfilUsuario perfil; // O enum de PerfilUsuario
    private String token; // O token JWT
}