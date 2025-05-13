package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PecaMaterialRequestDTO {

    private String codigo;
    private String descricao;
    private Double preco;
    private Integer estoque;
}