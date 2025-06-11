package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.PrioridadeOS;
import com.codagis.nordeste_servicos.model.StatusOS;
import com.codagis.nordeste_servicos.model.Usuario;
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

    // CAMPO ALTERADO AQUI:
    // Era: private Long tecnicoAtribuidoId;
    // Agora:
    private TecnicoDTO tecnicoAtribuido; // <<< MUDE PARA RECEBER O OBJETO ANINHADO

    private String problemaRelatado;
    private LocalDateTime dataAgendamento; // Opcional
    private PrioridadeOS prioridade;

    // Campos de execução/atualização (Técnico/Admin)
    private StatusOS status;
    private String analiseFalha;
    private String solucaoAplicada;
}