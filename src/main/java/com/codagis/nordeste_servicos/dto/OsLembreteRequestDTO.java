package com.codagis.nordeste_servicos.dto;

import lombok.Data;

@Data
public class OsLembreteRequestDTO {
    /** Se false, remove o lembrete. */
    private boolean ativo;
    /** Dias corridos após a data de fechamento (obrigatório se ativo = true). */
    private Integer diasAposFechamento;
}
