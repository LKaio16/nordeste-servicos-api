package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OsDashboardStatsDTO {
    private Long totalOs;
    private Long osEmAndamento;
    private Long osPendentes;
    private Long osAbertas;
    private Long osConcluidas;
    private Long totalClientes;
    private Long totalEquipamentos;
    /** Lembretes ativos com data alvo nos próximos 7 dias (inclui hoje). */
    private Long lembretesProximos7Dias;
    /** Lembretes ativos cuja data alvo já passou (antes de hoje). */
    private Long lembretesAtrasados;
    private List<DashboardTecnicoStatsDTO> osPorTecnico;
    private List<OrdemServicoResponseDTO> ordensRecentes;
}

