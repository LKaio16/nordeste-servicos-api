package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroTempoRequestDTO {

    private Long ordemServicoId;
    private Long tecnicoId;
    private Long tipoServicoId;
    private LocalDateTime horaInicio;
    private LocalDateTime horaTermino; // Opcional ao iniciar o timer
    // O campo horasTrabalhadas será calculado no serviço
}