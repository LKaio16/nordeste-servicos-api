package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.StatusOrcamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoRequestDTO {

    private Long clienteId;
    private Long ordemServicoOrigemId;
    private LocalDate dataValidade;
    private LocalDateTime dataHoraEmissao;
    private String observacoesCondicoes;
    private StatusOrcamento status;

}