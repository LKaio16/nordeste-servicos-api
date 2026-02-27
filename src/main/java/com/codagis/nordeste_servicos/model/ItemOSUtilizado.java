package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_os_utilizado", indexes = {
    @Index(name = "idx_item_os_ordem_servico", columnList = "ordem_servico_id"),
    @Index(name = "idx_item_os_peca_material", columnList = "peca_material_id"),
    @Index(name = "idx_item_os_qtd_requisitada", columnList = "quantidade_requisitada"),
    @Index(name = "idx_item_os_qtd_utilizada", columnList = "quantidade_utilizada"),
    @Index(name = "idx_item_os_qtd_devolvida", columnList = "quantidade_devolvida")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemOSUtilizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    @ManyToOne
    @JoinColumn(name = "peca_material_id", nullable = false)
    private PecaMaterial pecaMaterial;

    private Integer quantidadeRequisitada; // Quantidade inicialmente requisitada (pode ser null)
    private Integer quantidadeUtilizada; // Quantidade efetivamente utilizada
    private Integer quantidadeDevolvida; // Quantidade devolvida (pode ser null/0)

    // O Lombok cuida de Getters e Setters, etc.
}