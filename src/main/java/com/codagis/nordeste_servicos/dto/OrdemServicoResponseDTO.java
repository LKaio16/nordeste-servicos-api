package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.PrioridadeOS; // <-- IMPORTAR O ENUM
import com.codagis.nordeste_servicos.model.StatusOS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoResponseDTO {

    private Long id;
    private String numeroOS;
    private StatusOS status;
    private PrioridadeOS prioridade; // <-- ADICIONAR ESTE CAMPO
    private LocalDateTime dataAbertura;
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataFechamento;
    private LocalDateTime dataHoraEmissao;

    private Long clienteId;
    private String nomeCliente;
    private Long equipamentoId;
    private String descricaoEquipamento;
    private Long tecnicoAtribuidoId;
    private String nomeTecnicoAtribuido;

    private String problemaRelatado;
    private String analiseFalha;
    private String solucaoAplicada;
}