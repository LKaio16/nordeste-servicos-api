package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.PrioridadeOS;
import com.codagis.nordeste_servicos.model.StatusOS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoRequestDTO {

    private EntidadeIdDTO cliente;

    private EntidadeIdDTO equipamento;

    private EntidadeIdDTO tecnicoAtribuido;

    private String problemaRelatado;
    private LocalDateTime dataAgendamento;
    private PrioridadeOS prioridade;

    private StatusOS status;
    private String analiseFalha;
    private String solucaoAplicada;
}