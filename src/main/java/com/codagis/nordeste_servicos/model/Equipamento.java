package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Equipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    private String marcaModelo;

    @Column(unique = true) // Chassi/Número de Série geralmente é único
    private String numeroSerieChassi;

    private Double horimetro; // Pode ser null se não aplicável

    @ManyToOne // Muitos Equipamentos para Um Cliente
    @JoinColumn(name = "cliente_id", nullable = false) // Coluna na tabela Equipamento que referencia Cliente
    private Cliente cliente;

    // O Lombok cuida de Getters e Setters, etc.
}