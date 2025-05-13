package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemOSUtilizadoRequestDTO {

    private Long ordemServicoId; // Será preenchido pela URL no controller
    private Long pecaMaterialId;
    private Integer quantidadeRequisitada; // Opcional
    private Integer quantidadeUtilizada; // Obrigatório
    private Integer quantidadeDevolvida; // Opcional
}