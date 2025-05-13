package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.StatusOS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoRequestDTO {

    // Campos de criação (Admin)
    private Long clienteId;
    private Long equipamentoId;
    private Long tecnicoAtribuidoId; // Pode ser null na criação inicial
    private String problemaRelatado;
    private LocalDateTime dataAgendamento; // Opcional
    // Opcional: Status inicial, Prioridade

    // Campos de execução/atualização (Técnico/Admin)
    private StatusOS status; // Para atualizar o status
    private String analiseFalha; // Preenchido na execução
    private String solucaoAplicada; // Preenchido na execução
    // Note: Itens utilizados, registros de tempo/deslocamento, fotos, assinatura
    // Serão gerenciados por endpoints/DTOs separados ou em DTOs de atualização mais complexos
    // para evitar DTOs gigantescos.
}