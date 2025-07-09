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
    private String fotoBase64; // Enviará a imagem de volta para o app
    private String descricao; // E a descrição
    private String nomeArquivoOriginal;
    private String tipoConteudo;
    private Long tamanhoArquivo;
    private LocalDateTime dataUpload;
    private String caminhoTemporario;
}