package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemOrcamentoRequestDTO {

    private Long orcamentoId; // Será preenchido pela URL no controller
    private Long pecaMaterialId; // Opcional
    private Long tipoServicoId; // Opcional
    private String descricao; // Obrigatório se pecaMaterialId e tipoServicoId forem null
    private Double quantidade; // Obrigatório
    private Double valorUnitario; // Obrigatório
    // O subtotal será calculado no serviço
}