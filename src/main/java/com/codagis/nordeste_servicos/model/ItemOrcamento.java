package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_orcamento", indexes = {
    @Index(name = "idx_item_orc_orcamento", columnList = "orcamento_id"),
    @Index(name = "idx_item_orc_peca_material", columnList = "peca_material_id"),
    @Index(name = "idx_item_orc_tipo_servico", columnList = "tipo_servico_id"),
    @Index(name = "idx_item_orc_descricao", columnList = "descricao"),
    @Index(name = "idx_item_orc_quantidade", columnList = "quantidade"),
    @Index(name = "idx_item_orc_valor_unitario", columnList = "valor_unitario"),
    @Index(name = "idx_item_orc_subtotal", columnList = "subtotal")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private Orcamento orcamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peca_material_id")
    private PecaMaterial pecaMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_servico_id")
    private TipoServico tipoServico;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private Double quantidade;

    @Column(nullable = false)
    private Double valorUnitario;

    private Double subtotal;

}