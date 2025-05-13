package com.codagis.nordeste_servicos.dto;

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
    private LocalDateTime dataAbertura;
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataFechamento;
    private LocalDateTime dataHoraEmissao;

    private Long clienteId;
    private String nomeCliente; // Opcional: Incluir nome para facilitar a visualização
    private Long equipamentoId;
    private String descricaoEquipamento; // Opcional: Incluir descrição
    private Long tecnicoAtribuidoId; // ID do técnico
    private String nomeTecnicoAtribuido; // Opcional: Incluir nome do técnico

    private String problemaRelatado;
    private String analiseFalha;
    private String solucaoAplicada;

    // Não incluiremos os detalhes das listas (registros de tempo, peças, etc.) neste DTO principal
    // Para obter esses detalhes, haverá endpoints específicos (ex: GET /api/os/{id}/registros-tempo)
}