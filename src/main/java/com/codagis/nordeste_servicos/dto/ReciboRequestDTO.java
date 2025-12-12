package com.codagis.nordeste_servicos.dto;

public class ReciboRequestDTO {
    private Double valor;
    private String cliente;
    private String referenteA;

    public ReciboRequestDTO() {
    }

    public ReciboRequestDTO(Double valor, String cliente, String referenteA) {
        this.valor = valor;
        this.cliente = cliente;
        this.referenteA = referenteA;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getReferenteA() {
        return referenteA;
    }

    public void setReferenteA(String referenteA) {
        this.referenteA = referenteA;
    }
}

