package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.StatusOrcamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoResponseDTO {

    private Long id;
    private String numeroOrcamento;
    private LocalDate dataCriacao;
    private LocalDate dataValidade;
    private StatusOrcamento status;

    private Long clienteId;
    private String nomeCliente; // Opcional
    private Long ordemServicoOrigemId; // ID da OS de origem, se houver

    private String observacoesCondicoes;
    private Double valorTotal;

    // Não incluiremos a lista de ItemOrcamento neste DTO principal
    // Para obter os itens, haverá um endpoint específico (ex: GET /api/orcamentos/{id}/itens)
}