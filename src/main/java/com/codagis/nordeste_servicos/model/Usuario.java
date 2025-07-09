package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
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
    @Column(columnDefinition = "TEXT")
    private String fotoPerfil;
    // O Lombok cuida de Getters e Setters, etc.
}