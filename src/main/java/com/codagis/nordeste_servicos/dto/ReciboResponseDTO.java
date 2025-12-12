package com.codagis.nordeste_servicos.dto;

import java.time.LocalDateTime;

public class ReciboResponseDTO {
    private Long id;
    private Double valor;
    private String cliente;
    private String referenteA;
    private LocalDateTime dataCriacao;
    private String numeroRecibo;

    public ReciboResponseDTO() {
    }

    public ReciboResponseDTO(Long id, Double valor, String cliente, String referenteA, LocalDateTime dataCriacao, String numeroRecibo) {
        this.id = id;
        this.valor = valor;
        this.cliente = cliente;
        this.referenteA = referenteA;
        this.dataCriacao = dataCriacao;
        this.numeroRecibo = numeroRecibo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getNumeroRecibo() {
        return numeroRecibo;
    }

    public void setNumeroRecibo(String numeroRecibo) {
        this.numeroRecibo = numeroRecibo;
    }
}

