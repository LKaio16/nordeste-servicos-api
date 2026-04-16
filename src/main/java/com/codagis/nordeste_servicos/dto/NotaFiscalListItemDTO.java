package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.TipoNotaFiscal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaFiscalListItemDTO {
    private Long id;
    private TipoNotaFiscal tipo;
    private String numeroNota;
    private String nomeEmitente;
    private String fornecedorNome;
    private String clienteNome;
    private LocalDate dataEmissao;
    private BigDecimal valorTotal;
}
