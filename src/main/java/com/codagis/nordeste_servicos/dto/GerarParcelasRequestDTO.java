package com.codagis.nordeste_servicos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GerarParcelasRequestDTO {

    @NotNull(message = "Quantidade de parcelas é obrigatória")
    @Min(value = 1, message = "Mínimo 1 parcela")
    private Integer quantidade;

    @NotNull(message = "Data do primeiro vencimento é obrigatória")
    private LocalDate primeiraDataVencimento;
}
