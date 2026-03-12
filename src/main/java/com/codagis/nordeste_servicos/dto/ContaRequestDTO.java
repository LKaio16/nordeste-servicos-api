package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.CategoriaFinanceira;
import com.codagis.nordeste_servicos.model.FormaPagamento;
import com.codagis.nordeste_servicos.model.StatusConta;
import com.codagis.nordeste_servicos.model.TipoConta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaRequestDTO {

    @NotNull(message = "Tipo é obrigatório")
    private TipoConta tipo;

    private Long clienteId;
    private Long fornecedorId;

    @NotNull(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    private BigDecimal valor;

    private BigDecimal valorPago;

    @NotNull(message = "Data de vencimento é obrigatória")
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    @NotNull(message = "Status é obrigatório")
    private StatusConta status;

    private String categoria;
    private CategoriaFinanceira categoriaFinanceira;
    private String subcategoria;
    private FormaPagamento formaPagamento;
    private String observacoes;
}
