package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.FormaPagamento;
import com.codagis.nordeste_servicos.model.TipoNotaFiscal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaFiscalResponseDTO {

    private Long id;
    private TipoNotaFiscal tipo;
    private Long fornecedorId;
    private String fornecedorNome;
    private Long clienteId;
    private String clienteNome;
    private String nomeEmitente;
    private String cnpjEmitente;
    private LocalDate dataEmissao;
    private String numeroNota;
    private BigDecimal valorTotal;
    private FormaPagamento formaPagamento;
    private String descricao;
    private String observacoes;
}
