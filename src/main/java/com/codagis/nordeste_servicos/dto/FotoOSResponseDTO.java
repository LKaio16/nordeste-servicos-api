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
    private String urlAcesso; // URL para baixar/visualizar a foto
    private String nomeArquivoOriginal;
    private String tipoConteudo;
    private Long tamanhoArquivo;
    private LocalDateTime dataUpload;
    // Opcional: String descricao;
}