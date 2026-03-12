package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "conta",
       indexes = {
           @Index(name = "idx_conta_cliente", columnList = "cliente_id"),
           @Index(name = "idx_conta_fornecedor", columnList = "fornecedor_id"),
           @Index(name = "idx_conta_tipo", columnList = "tipo"),
           @Index(name = "idx_conta_status", columnList = "status"),
           @Index(name = "idx_conta_vencimento", columnList = "data_vencimento")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoConta tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPago = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusConta status;

    @Column(length = 100)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CategoriaFinanceira categoriaFinanceira;

    @Column(length = 100)
    private String subcategoria;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FormaPagamento formaPagamento;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}
