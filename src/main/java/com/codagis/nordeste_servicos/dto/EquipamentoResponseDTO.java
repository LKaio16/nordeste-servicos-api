package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipamentoResponseDTO {

    private Long id;
    private String tipo;
    private String marcaModelo;
    private String numeroSerieChassi;
    private Double horimetro;
    private Long clienteId; // ID do Cliente associado
    // Poderíamos adicionar ClienteResponseDTO cliente; aqui se quiséssemos mais detalhes do cliente
}