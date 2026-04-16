package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoDashboardStatsDTO {
    private Long totalOrcamentos;
    private Long orcamentosAprovados;
    private Long orcamentosRejeitados;
}
