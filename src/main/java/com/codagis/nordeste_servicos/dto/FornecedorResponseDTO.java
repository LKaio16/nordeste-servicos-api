package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.StatusFornecedor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorResponseDTO {

    private Long id;
    private String nome;
    private String cnpj;
    private String email;
    private String telefone;
    private String endereco;
    private String cidade;
    private String estado;
    private StatusFornecedor status;
    private String observacoes;
}
