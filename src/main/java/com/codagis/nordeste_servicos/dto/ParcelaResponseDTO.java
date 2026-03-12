package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.StatusConta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParcelaResponseDTO {

    private Long id;
    private Long contaId;
    private Integer numeroParcela;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private BigDecimal valorPago;
    private LocalDate dataPagamento;
    private StatusConta status;
}
