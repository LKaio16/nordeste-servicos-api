package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "recibos", indexes = {
    @Index(name = "idx_recibo_valor", columnList = "valor"),
    @Index(name = "idx_recibo_cliente", columnList = "cliente"),
    @Index(name = "idx_recibo_referente", columnList = "referentea"),
    @Index(name = "idx_recibo_data_criacao", columnList = "data_criacao"),
    @Index(name = "idx_recibo_numero", columnList = "numero_recibo")
})
public class Recibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private String cliente;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String referenteA;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private String numeroRecibo; // Número único do recibo
}

