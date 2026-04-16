package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cliente", indexes = {
    @Index(name = "idx_cliente_tipo", columnList = "tipo_cliente"),
    @Index(name = "idx_cliente_nome", columnList = "nome_completo"),
    @Index(name = "idx_cliente_cpf_cnpj", columnList = "cpf_cnpj"),
    @Index(name = "idx_cliente_email", columnList = "email"),
    @Index(name = "idx_cliente_telefone_principal", columnList = "telefone_principal"),
    @Index(name = "idx_cliente_telefone_adicional", columnList = "telefone_adicional"),
    @Index(name = "idx_cliente_cep", columnList = "cep"),
    @Index(name = "idx_cliente_rua", columnList = "rua"),
    @Index(name = "idx_cliente_numero", columnList = "numero"),
    @Index(name = "idx_cliente_complemento", columnList = "complemento"),
    @Index(name = "idx_cliente_bairro", columnList = "bairro"),
    @Index(name = "idx_cliente_cidade", columnList = "cidade"),
    @Index(name = "idx_cliente_estado", columnList = "estado"),
    @Index(name = "idx_cliente_cidade_estado", columnList = "cidade, estado")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCliente tipoCliente;

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(unique = true, nullable = false)
    private String cpfCnpj;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telefonePrincipal;

    private String telefoneAdicional;

    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private String rua;

    @Column(nullable = false)
    private String numero;

    private String complemento;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado;

}
