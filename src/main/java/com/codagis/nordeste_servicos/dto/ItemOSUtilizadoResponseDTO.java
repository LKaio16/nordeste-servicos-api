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
    private String codigoPecaMaterial; // Opcional: Incluir código para facilitar
    private String descricaoPecaMaterial; // Opcional: Incluir descrição
    private Double precoUnitarioPecaMaterial; // Opcional: Incluir preço unitário
    private Integer quantidadeRequisitada;
    private Integer quantidadeUtilizada;
    private Integer quantidadeDevolvida;
    // Opcional: Calcular Subtotal (quantidadeUtilizada * precoUnitario) no serviço e adicionar aqui
    // private Double subtotal;
}