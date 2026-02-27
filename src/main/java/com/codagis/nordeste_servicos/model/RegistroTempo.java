package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registro_tempo", indexes = {
    @Index(name = "idx_reg_tempo_ordem_servico", columnList = "ordem_servico_id"),
    @Index(name = "idx_reg_tempo_tecnico", columnList = "tecnico_id"),
    @Index(name = "idx_reg_tempo_hora_inicio", columnList = "hora_inicio"),
    @Index(name = "idx_reg_tempo_hora_termino", columnList = "hora_termino"),
    @Index(name = "idx_reg_tempo_horas_trabalhadas", columnList = "horas_trabalhadas"),
    @Index(name = "idx_reg_tempo_os_tec_hora", columnList = "ordem_servico_id, tecnico_id, hora_termino")
})
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