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
public class RegistroTempo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    @ManyToOne
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Usuario tecnico; // O técnico que registrou o tempo

    @Column(nullable = false)
    private LocalDateTime horaInicio;

    private LocalDateTime horaTermino; // Pode ser null se o timer estiver ativo

    private Double horasTrabalhadas; // Calculado com base em inicio e termino

    // O Lombok cuida de Getters e Setters, etc.
}