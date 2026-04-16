package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.codagis.nordeste_servicos.model.ItemOrcamento;

@Entity
@Table(name = "orcamento", indexes = {
    @Index(name = "idx_orcamento_numero", columnList = "numero_orcamento"),
    @Index(name = "idx_orcamento_data_criacao", columnList = "data_criacao"),
    @Index(name = "idx_orcamento_data_validade", columnList = "data_validade"),
    @Index(name = "idx_orcamento_data_hora_emissao", columnList = "data_hora_emissao"),
    @Index(name = "idx_orcamento_status", columnList = "status"),
    @Index(name = "idx_orcamento_cliente", columnList = "cliente_id"),
    @Index(name = "idx_orcamento_ordem_servico", columnList = "ordem_servico_id"),
    @Index(name = "idx_orcamento_observacoes", columnList = "observacoes_condicoes"),
    @Index(name = "idx_orcamento_valor_total", columnList = "valor_total"),
    @Index(name = "idx_orcamento_cliente_status", columnList = "cliente_id, status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroOrcamento;

    @Column(nullable = false)
    private LocalDate dataCriacao;

    @Column(nullable = false)
    private LocalDate dataValidade;

    @Column(name = "data_hora_emissao")
    private LocalDateTime dataHoraEmissao;

    @Enumerated(EnumType.STRING)
    private StatusOrcamento status;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToOne
    @JoinColumn(name = "ordem_servico_id")
    private OrdemServico ordemServicoOrigem;

    @Column(columnDefinition = "TEXT")
    private String observacoesCondicoes;

    private Double valorTotal;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOrcamento> itensOrcamento;

}