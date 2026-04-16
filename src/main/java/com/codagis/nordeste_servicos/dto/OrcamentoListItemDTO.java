package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.StatusOrcamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoListItemDTO {
    private Long id;
    private String numeroOrcamento;
    private StatusOrcamento status;
    private String nomeCliente;
    private LocalDate dataValidade;
    private Double valorTotal;
}
