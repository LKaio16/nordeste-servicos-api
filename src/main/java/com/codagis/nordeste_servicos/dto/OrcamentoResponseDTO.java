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
public class OrcamentoResponseDTO {

    private Long id;
    private String numeroOrcamento;
    private LocalDate dataCriacao;
    private LocalDate dataValidade;
    private LocalDateTime dataHoraEmissao;
    private StatusOrcamento status;

    private Long clienteId;
    private String nomeCliente;
    private Long ordemServicoOrigemId;

    private String observacoesCondicoes;
    private Double valorTotal;

}