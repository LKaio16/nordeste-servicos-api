package com.codagis.nordeste_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO {

    private String nomeRazaoSocial;
    private String endereco;
    private String telefone;
    private String email;
    private String cnpjCpf;

}