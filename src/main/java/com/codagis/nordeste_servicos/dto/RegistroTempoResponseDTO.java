package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroTempoResponseDTO {

    private Long id;
    private Long ordemServicoId;
    private Long tecnicoId;
    private String nomeTecnico; // Opcional
    private Long tipoServicoId;
    private String descricaoTipoServico; // Opcional
    private LocalDateTime horaInicio;
    private LocalDateTime horaTermino;
    private Double horasTrabalhadas;
}