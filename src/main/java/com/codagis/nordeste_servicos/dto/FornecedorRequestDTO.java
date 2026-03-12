package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.StatusFornecedor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200)
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    @Size(max = 18)
    private String cnpj;

    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String telefone;

    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 200)
    private String endereco;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100)
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2)
    private String estado;

    @NotNull(message = "Status é obrigatório")
    private StatusFornecedor status;

    @Size(max = 500)
    private String observacoes;
}
