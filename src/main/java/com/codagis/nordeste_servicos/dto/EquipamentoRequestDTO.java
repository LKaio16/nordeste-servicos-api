package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipamentoRequestDTO {

    private String tipo;
    private String marcaModelo;
    private String numeroSerieChassi;
    private Double horimetro;
    private Long clienteId; // Para associar ao Cliente
}