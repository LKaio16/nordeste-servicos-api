package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.StatusOrcamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoRequestDTO {

    // Opcional: String numeroOrcamento; // Se permitir definir manualmente (geralmente auto)
    private Long clienteId;
    private Long ordemServicoOrigemId; // Opcional
    private LocalDate dataValidade;
    private String observacoesCondicoes;
    private StatusOrcamento status; // Pode ser definido na criação ou atualizado
    // O valorTotal será calculado no serviço, e os ItemOrcamento gerenciados em endpoints separados
}