package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fornecedor",
       indexes = {
           @Index(name = "idx_fornecedor_cnpj", columnList = "cnpj"),
           @Index(name = "idx_fornecedor_email", columnList = "email")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_fornecedor_cnpj", columnNames = "cnpj")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(nullable = false, length = 200)
    private String endereco;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusFornecedor status;

    @Column(length = 500)
    private String observacoes;
}
