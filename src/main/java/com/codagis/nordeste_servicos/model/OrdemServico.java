package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ordem_servico", indexes = {
    @Index(name = "idx_os_numero", columnList = "numeroos"),
    @Index(name = "idx_os_status", columnList = "status"),
    @Index(name = "idx_os_data_abertura", columnList = "data_abertura"),
    @Index(name = "idx_os_data_agendamento", columnList = "data_agendamento"),
    @Index(name = "idx_os_data_fechamento", columnList = "data_fechamento"),
    @Index(name = "idx_os_data_hora_emissao", columnList = "data_hora_emissao"),
    @Index(name = "idx_os_cliente", columnList = "cliente_id"),
    @Index(name = "idx_os_equipamento", columnList = "equipamento_id"),
    @Index(name = "idx_os_tecnico", columnList = "tecnico_id"),
    @Index(name = "idx_os_prioridade", columnList = "prioridade"),
    @Index(name = "idx_os_problema_relatado", columnList = "problema_relatado"),
    @Index(name = "idx_os_analise_falha", columnList = "analise_falha"),
    @Index(name = "idx_os_solucao_aplicada", columnList = "solucao_aplicada"),
    @Index(name = "idx_os_cliente_status", columnList = "cliente_id, status"),
    @Index(name = "idx_os_tecnico_status", columnList = "tecnico_id, status"),
    @Index(name = "idx_os_lembrete_ativo", columnList = "lembrete_ativo"),
    @Index(name = "idx_os_lembrete_data_alvo", columnList = "lembrete_data_alvo")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroOS;

    @Enumerated(EnumType.STRING)
    private StatusOS status;

    private LocalDateTime dataAbertura;
    private LocalDateTime dataAgendamento;
    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;
    @Column(name = "data_hora_emissao")
    private LocalDateTime dataHoraEmissao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id")
    private Usuario tecnicoAtribuido;

    @Column(columnDefinition = "TEXT")
    private String problemaRelatado;

    @Column(columnDefinition = "TEXT")
    private String analiseFalha;

    @Column(columnDefinition = "TEXT")
    private String solucaoAplicada;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RegistroTempo> registrosTempo;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RegistroDeslocamento> registrosDeslocamento;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemOSUtilizado> itensUtilizados;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FotoOS> fotos;

    @OneToOne(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AssinaturaOS assinatura;

    @Enumerated(EnumType.STRING)
    private PrioridadeOS prioridade;

    @Column(name = "lembrete_ativo", nullable = false)
    private boolean lembreteAtivo = false;

    @Column(name = "lembrete_dias_apos_fechamento")
    private Integer lembreteDiasAposFechamento;

    @Column(name = "lembrete_data_alvo")
    private LocalDate lembreteDataAlvo;

}