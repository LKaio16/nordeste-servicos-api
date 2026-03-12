package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.FormaPagamento;
import com.codagis.nordeste_servicos.model.TipoNotaFiscal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaFiscalRequestDTO {

    @NotNull(message = "Tipo é obrigatório")
    private TipoNotaFiscal tipo;

    private Long fornecedorId;
    private Long clienteId;

    private String nomeEmitente;
    private String cnpjEmitente;

    @NotNull(message = "Data de emissão é obrigatória")
    private LocalDate dataEmissao;

    @NotNull(message = "Número da nota é obrigatório")
    private String numeroNota;

    @NotNull(message = "Valor total é obrigatório")
    private BigDecimal valorTotal;

    private FormaPagamento formaPagamento;
    private String descricao;
    private String observacoes;
}
