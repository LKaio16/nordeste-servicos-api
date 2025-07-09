package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssinaturaOSRequestDTO {
    private String assinaturaClienteBase64;
    private String nomeClienteResponsavel;
    private String documentoClienteResponsavel;
    private String assinaturaTecnicoBase64;
    private String nomeTecnicoResponsavel;
}