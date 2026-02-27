package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "peca_material", indexes = {
    @Index(name = "idx_peca_codigo", columnList = "codigo"),
    @Index(name = "idx_peca_descricao", columnList = "descricao"),
    @Index(name = "idx_peca_preco", columnList = "preco"),
    @Index(name = "idx_peca_estoque", columnList = "estoque")
})
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