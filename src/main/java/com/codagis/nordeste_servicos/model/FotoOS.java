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
public class FotoOS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    private String caminhoArquivo; // Caminho local no servidor ou URL para storage em nuvem

    private String nomeArquivoOriginal; // Nome do arquivo no upload
    private String tipoConteudo; // MIME type (ex: image/jpeg)
    private Long tamanhoArquivo; // Em bytes

    private LocalDateTime dataUpload;

    // Opcional: Descrição da foto
    // private String descricao;

    // O Lombok cuida de Getters e Setters, etc.
}