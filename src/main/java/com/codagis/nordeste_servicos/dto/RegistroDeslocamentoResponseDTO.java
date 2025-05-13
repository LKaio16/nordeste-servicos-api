package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDeslocamentoResponseDTO {

    private Long id;
    private Long ordemServicoId;
    private Long tecnicoId;
    private String nomeTecnico; // Opcional
    private LocalDate data;
    private String placaVeiculo;
    private Double kmInicial;
    private Double kmFinal;
    private Double totalKm;
    private String saidaDe;
    private String chegadaEm;
}