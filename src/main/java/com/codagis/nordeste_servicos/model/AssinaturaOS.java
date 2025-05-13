package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssinaturaOS {

    @Id
    // Usamos GenerationType.IDENTITY ou similar se a chave for independente
    // Se a chave primária for a mesma da OS, podemos usar @Id @MapsId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne // Uma Assinatura para Uma Ordem de Serviço
    @JoinColumn(name = "ordem_servico_id", nullable = false) // Coluna na tabela AssinaturaOS
    private OrdemServico ordemServico;

    private String caminhoArquivo; // Caminho ou URL para a imagem da assinatura

    private String tipoConteudo; // MIME type (ex: image/png)
    private Long tamanhoArquivo; // Em bytes

    private LocalDateTime dataHoraColeta; // Quando a assinatura foi coletada

    // O Lombok cuida de Getters e Setters, etc.
}