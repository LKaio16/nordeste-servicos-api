package com.codagis.nordeste_servicos.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssinaturaOSResponseDTO {
    private Long id;
    private Long ordemServicoId;
    private String assinaturaClienteBase64;
    private String nomeClienteResponsavel;
    private String documentoClienteResponsavel;
    private String assinaturaTecnicoBase64;
    private String nomeTecnicoResponsavel;
    private LocalDateTime dataHoraColeta;
}