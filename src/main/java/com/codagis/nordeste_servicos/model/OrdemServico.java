package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List; // Para coleções de entidades relacionadas

@Entity
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
    private String numeroOS; // Número automático da OS

    @Enumerated(EnumType.STRING) // Armazena o enum como String
    private StatusOS status; // Enum para representar os status da OS

    private LocalDateTime dataAbertura;
    private LocalDateTime dataAgendamento; // Opcional
    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento; // Preenchido ao encerrar
    @Column(name = "data_hora_emissao")
    private LocalDateTime dataHoraEmissao; // Data/Hora de emissão do relatório (pode ser gerado na hora de emitir o PDF)

    @ManyToOne(fetch = FetchType.LAZY) // Cliente
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY) // Equipamento
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @ManyToOne(fetch = FetchType.LAZY) // O técnico que está atribuído a esta OS
    @JoinColumn(name = "tecnico_id") // Pode ser null se a OS ainda não foi atribuída
    private Usuario tecnicoAtribuido;

    @Column(columnDefinition = "TEXT") // Usar TEXT para textos longos
    private String problemaRelatado;

    @Column(columnDefinition = "TEXT")
    private String analiseFalha; // Preenchido pelo técnico

    @Column(columnDefinition = "TEXT")
    private String solucaoAplicada; // Preenchido pelo técnico

    // Relacionamentos com entidades de detalhes - DESCOMENTADOS E CONFIGURADOS
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

    // Campo Opcional: Prioridade - DESCOMENTADO
    @Enumerated(EnumType.STRING)
    private PrioridadeOS prioridade;

    // O Lombok cuida de Getters e Setters, etc.
}