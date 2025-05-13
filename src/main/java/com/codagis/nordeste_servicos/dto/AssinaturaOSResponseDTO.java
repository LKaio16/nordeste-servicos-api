package com.codagis.nordeste_servicos.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AssinaturaOSResponseDTO {

    private Long id;
    private Long ordemServicoId;
    private String urlAcesso; // URL para baixar/visualizar a assinatura
    private String tipoConteudo;
    private Long tamanhoArquivo;
    private LocalDateTime dataHoraColeta;
}