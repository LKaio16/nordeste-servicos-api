package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.PerfilUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {

    private String nome;
    private String cracha;
    private String email;
    private String senha;
    private PerfilUsuario perfil;
    private String fotoPerfil;
}