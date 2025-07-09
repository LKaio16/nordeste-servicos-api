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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    @Lob // Indica que é um Large Object
    @Column(columnDefinition = "TEXT", nullable = false) // Mapeia para um tipo de texto longo no banco
    private String fotoBase64; // Armazena a imagem como string Base64

    private String descricao; // Descrição da foto

    private String nomeArquivoOriginal;
    private String tipoConteudo;
    private Long tamanhoArquivo;

    private LocalDateTime dataUpload;
    private String caminhoTemporario;
}