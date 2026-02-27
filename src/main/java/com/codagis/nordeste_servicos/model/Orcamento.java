package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List; // Para a lista de itens do orçamento

import com.codagis.nordeste_servicos.model.ItemOrcamento; // <-- ADICIONE ESTA LINHA

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
    private String numeroOrcamento; // Número automático do orçamento

    @Column(nullable = false)
    private LocalDate dataCriacao;

    @Column(nullable = false)
    private LocalDate dataValidade;

    @Column(name = "data_hora_emissao")
    private LocalDateTime dataHoraEmissao; // Data/Hora de emissão do PDF (atualizada toda vez que o PDF é gerado)

    @Enumerated(EnumType.STRING) // Armazena o enum como String
    private StatusOrcamento status; // Enum para representar os status do orçamento

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToOne // Opcional: Se um orçamento foi gerado a partir de uma OS
    @JoinColumn(name = "ordem_servico_id") // Pode ser null
    private OrdemServico ordemServicoOrigem;

    @Column(columnDefinition = "TEXT")
    private String observacoesCondicoes; // Observações e condições do orçamento

    private Double valorTotal; // Calculado com base nos itens do orçamento

    // Relacionamento com os itens do orçamento
    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOrcamento> itensOrcamento;


    // O Lombok cuida de Getters e Setters, etc.
}