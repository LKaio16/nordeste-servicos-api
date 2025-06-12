package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.PrioridadeOS;
import com.codagis.nordeste_servicos.model.StatusOS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoRequestDTO {

    // --- CAMPOS AJUSTADOS PARA RECEBER OBJETOS ANINHADOS ---

    // Antes: private Long clienteId;
    private EntidadeIdDTO cliente; // <<< AJUSTADO

    // Antes: private Long equipamentoId;
    private EntidadeIdDTO equipamento; // <<< AJUSTADO

    // O seu campo de técnico já estava como um DTO, o que é ótimo.
    // Para consistência, você pode usar EntidadeIdDTO aqui também
    // se o TecnicoDTO só tiver o ID. Se tiver mais campos, mantenha TecnicoDTO.
    private EntidadeIdDTO tecnicoAtribuido; // <<< AJUSTADO PARA CONSISTÊNCIA


    private String problemaRelatado;
    private LocalDateTime dataAgendamento; // Opcional
    private PrioridadeOS prioridade;

    // Campos de execução/atualização (Técnico/Admin)
    private StatusOS status;
    private String analiseFalha;
    private String solucaoAplicada;
}