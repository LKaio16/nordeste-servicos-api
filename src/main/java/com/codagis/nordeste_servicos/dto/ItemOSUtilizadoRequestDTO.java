package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemOSUtilizadoRequestDTO {

    private Long ordemServicoId;
    private Long pecaMaterialId;
    private Integer quantidadeRequisitada;
    private Integer quantidadeUtilizada;
    private Integer quantidadeDevolvida;
}