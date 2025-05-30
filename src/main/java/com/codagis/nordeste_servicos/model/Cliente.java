package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data // Lombok: Gera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: Gera construtor sem argumentos
@AllArgsConstructor // Lombok: Gera construtor com todos os argumentos
@Getter // Lombok: Garante getters (redundante com @Data, mas explícito)
@Setter // Lombok: Garante setters (redundante com @Data, mas explícito)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // Mapeia o Enum como String no banco
    @Column(nullable = false)
    private TipoCliente tipoCliente;

    @Column(nullable = false)
    private String nomeCompleto; // Substitui nomeRazaoSocial

    @Column(unique = true, nullable = false) // Garante que CNPJ/CPF seja único e não nulo
    private String cpfCnpj; // Substitui cnpjCpf

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telefonePrincipal; // Substitui telefone

    private String telefoneAdicional; // Novo campo opcional

    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private String rua;

    @Column(nullable = false)
    private String numero;

    private String complemento; // Novo campo opcional

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado; // Ex: "CE", "SP"

    // Relacionamentos podem ser adicionados aqui depois
    // Exemplo: @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Equipamento> equipamentos = new ArrayList<>();
}

