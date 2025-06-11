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

    // Manter ClienteId e NomeCliente ou aninhar ClienteResponseDTO se necessário mais detalhes
    private Long clienteId;
    private String nomeCliente;

    // Manter EquipamentoId e DescricaoEquipamento ou aninhar EquipamentoResponseDTO
    private Long equipamentoId;
    private String descricaoEquipamento;

    // --- CAMPO DO TÉCNICO ATRIBUÍDO AGORA É UM USUARIORESPONSEDTO ---
    private UsuarioResponseDTO tecnicoAtribuido; // Usando o DTO que você já tem

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