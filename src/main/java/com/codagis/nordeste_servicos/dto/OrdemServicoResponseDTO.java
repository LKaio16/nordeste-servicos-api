package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.PrioridadeOS;
import com.codagis.nordeste_servicos.model.StatusOS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoResponseDTO {

    private Long id;
    private String numeroOS;
    private StatusOS status;
    private PrioridadeOS prioridade;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataFechamento;
    private LocalDateTime dataHoraEmissao;

    private ClienteResponseDTO cliente;

    private EquipamentoResponseDTO equipamento;

    private UsuarioResponseDTO tecnicoAtribuido;

    private String problemaRelatado;
    private String analiseFalha;
    private String solucaoAplicada;

    private List<RegistroTempoResponseDTO> registrosTempo;
    private List<RegistroDeslocamentoResponseDTO> registrosDeslocamento;
    private List<ItemOSUtilizadoResponseDTO> itensUtilizados;
    private List<FotoOSResponseDTO> fotos;
    private AssinaturaOSResponseDTO assinatura;

    private boolean lembreteAtivo;
    private Integer lembreteDiasAposFechamento;
    private LocalDate lembreteDataAlvo;
}