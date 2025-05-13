package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Usar LAZY para evitar carregar todos os itens ao carregar o orçamento
    @JoinColumn(name = "orcamento_id", nullable = false)
    private Orcamento orcamento;

    @ManyToOne(fetch = FetchType.LAZY) // Opcional: Se o item for uma Peça/Material do cadastro
    @JoinColumn(name = "peca_material_id")
    private PecaMaterial pecaMaterial;

    @ManyToOne(fetch = FetchType.LAZY) // Opcional: Se o item for um Tipo de Serviço do cadastro
    @JoinColumn(name = "tipo_servico_id")
    private TipoServico tipoServico;

    @Column(nullable = false)
    private String descricao; // Descrição do item (pode vir do cadastro ou ser livre)

    @Column(nullable = false)
    private Double quantidade; // Quantidade do item (pode ser decimal)

    @Column(nullable = false)
    private Double valorUnitario; // Valor unitário do item

    private Double subtotal; // Calculado (quantidade * valorUnitario)

    // O Lombok cuida de Getters e Setters, etc.
}