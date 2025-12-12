package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OsDashboardStatsDTO {
    private Long totalOs;
    private Long osEmAndamento;
    private Long osPendentes;
    private Long osAbertas;
    private Long osConcluidas;
}

