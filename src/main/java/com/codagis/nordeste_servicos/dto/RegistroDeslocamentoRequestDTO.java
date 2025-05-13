package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDeslocamentoRequestDTO {

    private Long ordemServicoId;
    private Long tecnicoId;
    private LocalDate data;
    private String placaVeiculo;
    private Double kmInicial;
    private Double kmFinal;
    private String saidaDe;
    private String chegadaEm;
    // O campo totalKm será calculado no serviço
}