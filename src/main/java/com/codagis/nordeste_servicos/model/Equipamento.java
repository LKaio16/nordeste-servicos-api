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

    @Column(unique = true)
    private String numeroSerieChassi;

    private Double horimetro;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

}