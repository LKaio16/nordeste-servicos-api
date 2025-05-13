package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoServicoRequestDTO {

    private String descricao;
    // Opcional: Incluir precoPadrao se adicionado na entidade
    // private Double precoPadrao;
}