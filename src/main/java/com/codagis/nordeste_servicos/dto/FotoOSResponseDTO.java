package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FotoOSResponseDTO {

    private Long id;
    private Long ordemServicoId;
    private String fotoBase64; // Retornado apenas para registros antigos (quando fotoUrl é null)
    private String descricao;
    private String nomeArquivoOriginal;
    private String tipoConteudo;
    private Long tamanhoArquivo;
    private LocalDateTime dataUpload;
    private String caminhoTemporario;
    private String fotoUrl; // URL da imagem no Google Cloud Storage
}