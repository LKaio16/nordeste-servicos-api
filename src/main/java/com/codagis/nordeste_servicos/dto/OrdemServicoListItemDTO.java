package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.StatusOS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoListItemDTO {
    private Long id;
    private String numeroOS;
    private StatusOS status;
    private LocalDateTime dataAbertura;
    private String clienteNome;
    private String tecnicoNome;
}
