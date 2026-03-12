package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.CategoriaFinanceira;
import com.codagis.nordeste_servicos.model.FormaPagamento;
import com.codagis.nordeste_servicos.model.StatusConta;
import com.codagis.nordeste_servicos.model.TipoConta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaResponseDTO {

    private Long id;
    private TipoConta tipo;
    private Long clienteId;
    private String clienteNome;
    private Long fornecedorId;
    private String fornecedorNome;
    private String descricao;
    private BigDecimal valor;
    private BigDecimal valorPago;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private StatusConta status;
    private String categoria;
    private CategoriaFinanceira categoriaFinanceira;
    private String subcategoria;
    private FormaPagamento formaPagamento;
    private String observacoes;
}
