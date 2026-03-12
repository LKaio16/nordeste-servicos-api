package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Nota fiscal de entrada ou saída - apenas campos livres, sem vínculo com itens/estoque.
 */
@Entity
@Table(name = "nota_fiscal",
       indexes = {
           @Index(name = "idx_nota_numero", columnList = "numero_nota"),
           @Index(name = "idx_nota_tipo", columnList = "tipo"),
           @Index(name = "idx_nota_fornecedor", columnList = "fornecedor_id"),
           @Index(name = "idx_nota_cliente", columnList = "cliente_id")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoNotaFiscal tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(length = 200)
    private String nomeEmitente;

    @Column(length = 18)
    private String cnpjEmitente;

    @Column(nullable = false)
    private LocalDate dataEmissao;

    @Column(nullable = false, length = 50)
    private String numeroNota;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FormaPagamento formaPagamento;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}
