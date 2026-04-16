package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.TipoCliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {

    private Long id;
    private TipoCliente tipoCliente;
    private String nomeCompleto;
    private String cpfCnpj;
    private String email;
    private String telefonePrincipal;
    private String telefoneAdicional;
    private String cep;
    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;

}
