package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FotoOSUploadRequestDTO {
    private String fotoBase64;
    private String descricao;
    private String nomeArquivoOriginal;
    private String tipoConteudo;
    private Long tamanhoArquivo;
}