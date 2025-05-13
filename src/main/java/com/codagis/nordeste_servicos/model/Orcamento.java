package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List; // Para a lista de itens do orçamento

import com.codagis.nordeste_servicos.model.ItemOrcamento; // <-- ADICIONE ESTA LINHA

@Entity
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