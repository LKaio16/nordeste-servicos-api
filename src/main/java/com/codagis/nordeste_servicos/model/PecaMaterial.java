package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PecaMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false) // Código da peça/material deve ser único e obrigatório
    private String codigo;

    @Column(nullable = false)
    private String descricao;

    private Double preco; // Preço unitário da peça/material

    private Integer estoque; // Quantidade em estoque (opcional, pode ser null)


}