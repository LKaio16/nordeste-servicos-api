package com.codagis.nordeste_servicos.dto;

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
public class ContaListItemDTO {
    private Long id;
    private TipoConta tipo;
    private String descricao;
    private String clienteNome;
    private String fornecedorNome;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private StatusConta status;
}
