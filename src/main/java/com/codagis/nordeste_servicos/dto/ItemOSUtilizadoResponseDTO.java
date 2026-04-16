package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemOSUtilizadoResponseDTO {

    private Long id;
    private Long ordemServicoId;
    private Long pecaMaterialId;
    private String codigoPecaMaterial;
    private String descricaoPecaMaterial;
    private Double precoUnitarioPecaMaterial;
    private Integer quantidadeRequisitada;
    private Integer quantidadeUtilizada;
    private Integer quantidadeDevolvida;

}