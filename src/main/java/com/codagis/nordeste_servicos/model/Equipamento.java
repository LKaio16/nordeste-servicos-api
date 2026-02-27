package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipamento", indexes = {
    @Index(name = "idx_equip_tipo", columnList = "tipo"),
    @Index(name = "idx_equip_marca_modelo", columnList = "marca_modelo"),
    @Index(name = "idx_equip_numero_serie", columnList = "numero_serie_chassi"),
    @Index(name = "idx_equip_horimetro", columnList = "horimetro"),
    @Index(name = "idx_equip_cliente", columnList = "cliente_id")
})
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