package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemOrcamentoResponseDTO {

    private Long id;
    private Long orcamentoId;
    private Long pecaMaterialId;
    private String codigoPecaMaterial;
    private String descricaoPecaMaterial;
    private Long tipoServicoId;
    private String descricaoTipoServico;
    private String descricao;
    private Double quantidade;
    private Double valorUnitario;
    private Double subtotal;
}