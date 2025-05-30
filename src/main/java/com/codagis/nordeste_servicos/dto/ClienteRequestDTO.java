package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.TipoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO {

    @NotNull(message = "Tipo de cliente não pode ser nulo")
    private TipoCliente tipoCliente;

    @NotBlank(message = "Nome completo não pode ser vazio")
    @Size(min = 3, max = 255, message = "Nome completo deve ter entre 3 e 255 caracteres")
    private String nomeCompleto;

    @NotBlank(message = "CPF/CNPJ não pode ser vazio")
    // Adicionar validação específica para CPF/CNPJ se necessário (ex: @CPF ou @CNPJ)
    private String cpfCnpj;

    @NotBlank(message = "Email não pode ser vazio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "Telefone principal não pode ser vazio")
    private String telefonePrincipal;

    private String telefoneAdicional; // Opcional

    @NotBlank(message = "CEP não pode ser vazio")
    private String cep;

    @NotBlank(message = "Rua não pode ser vazia")
    private String rua;

    @NotBlank(message = "Número não pode ser vazio")
    private String numero;

    private String complemento; // Opcional

    @NotBlank(message = "Bairro não pode ser vazio")
    private String bairro;

    @NotBlank(message = "Cidade não pode ser vazia")
    private String cidade;

    @NotBlank(message = "Estado não pode ser vazio")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (ex: CE)")
    private String estado;
}

