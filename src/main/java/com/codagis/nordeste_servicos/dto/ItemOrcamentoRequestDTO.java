package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemOrcamentoRequestDTO {

    private Long orcamentoId;
    private Long pecaMaterialId;
    private Long tipoServicoId;
    private String descricao;
    private Double quantidade;
    private Double valorUnitario;

}