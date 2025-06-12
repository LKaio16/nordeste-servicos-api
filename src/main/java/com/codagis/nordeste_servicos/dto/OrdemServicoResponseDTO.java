package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.PrioridadeOS;
import com.codagis.nordeste_servicos.model.StatusOS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    // --- CAMPO DO CLIENTE AGORA É UM OBJETO ANINHADO ---
    private ClienteResponseDTO cliente;

    private EquipamentoResponseDTO equipamento;

    // --- CAMPO DO TÉCNICO ATRIBUÍDO AGORA É UM USUARIORESPONSEDTO ---
    private UsuarioResponseDTO tecnicoAtribuido;

    private String problemaRelatado;
    private String analiseFalha;
    private String solucaoAplicada;

    // DTOs aninhados existentes
    private List<RegistroTempoResponseDTO> registrosTempo;
    private List<RegistroDeslocamentoResponseDTO> registrosDeslocamento;
    private List<ItemOSUtilizadoResponseDTO> itensUtilizados;
    private List<FotoOSResponseDTO> fotos;
    private AssinaturaOSResponseDTO assinatura;
}