package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_servico", indexes = {
    @Index(name = "idx_tipo_servico_descricao", columnList = "descricao")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TipoServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Descrição do serviço deve ser única e obrigatória
    private String descricao;

    // Opcional: Adicionar campo para preço padrão do serviço, se houver
    // private Double precoPadrao;

    // O Lombok cuida de Getters e Setters, etc.
}