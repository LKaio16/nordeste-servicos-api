package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario", indexes = {
    @Index(name = "idx_usuario_nome", columnList = "nome"),
    @Index(name = "idx_usuario_cracha", columnList = "cracha"),
    @Index(name = "idx_usuario_email", columnList = "email"),
    @Index(name = "idx_usuario_senha", columnList = "senha"),
    @Index(name = "idx_usuario_perfil", columnList = "perfil")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String cracha;

    @Column(unique = true)
    private String email; // Usado para login/identificação

    private String senha;

    @Enumerated(EnumType.STRING) // Armazena o enum como String no banco
    private PerfilUsuario perfil; // Enum para ADMIN, TECNICO

    @Lob
    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;

    @Column(name = "foto_url", length = 512)
    private String fotoUrl;
}