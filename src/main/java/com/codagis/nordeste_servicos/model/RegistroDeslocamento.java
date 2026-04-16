package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "registro_deslocamento", indexes = {
    @Index(name = "idx_reg_desl_ordem_servico", columnList = "ordem_servico_id"),
    @Index(name = "idx_reg_desl_tecnico", columnList = "tecnico_id"),
    @Index(name = "idx_reg_desl_data", columnList = "data"),
    @Index(name = "idx_reg_desl_placa", columnList = "placa_veiculo"),
    @Index(name = "idx_reg_desl_km_inicial", columnList = "km_inicial"),
    @Index(name = "idx_reg_desl_km_final", columnList = "km_final"),
    @Index(name = "idx_reg_desl_total_km", columnList = "total_km"),
    @Index(name = "idx_reg_desl_saida", columnList = "saida_de"),
    @Index(name = "idx_reg_desl_chegada", columnList = "chegada_em")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegistroDeslocamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    @ManyToOne
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Usuario tecnico;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private String placaVeiculo;

    private Double kmInicial;
    private Double kmFinal;

    private Double totalKm;

    private String saidaDe;
    private String chegadaEm;

}