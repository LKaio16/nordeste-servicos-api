package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeRazaoSocial;

    private String endereco;

    private String telefone;

    private String email;

    @Column(unique = true) // Garante que CNPJ/CPF seja único
    private String cnpjCpf;

    // Relacionamentos (adicionaremos depois conforme avançamos nas outras entidades)
    // Exemplo: @OneToMany(mappedBy = "cliente")
    // private List<Equipamento> equipamentos;
}