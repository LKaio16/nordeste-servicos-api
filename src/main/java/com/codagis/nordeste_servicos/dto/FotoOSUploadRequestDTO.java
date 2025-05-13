package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// Não inclua o MultipartFile aqui, ele é tratado no Controller

@Data
@AllArgsConstructor
public class FotoOSUploadRequestDTO {
    // Exemplo: Se quiser enviar uma descrição junto com a foto
    // private String descricao;
    // O ordemServicoId virá da URL
}