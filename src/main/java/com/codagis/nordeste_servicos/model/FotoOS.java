package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fotoos", indexes = {
    @Index(name = "idx_foto_os_ordem_servico", columnList = "ordem_servico_id"),
    @Index(name = "idx_foto_os_descricao", columnList = "descricao"),
    @Index(name = "idx_foto_os_nome_arquivo", columnList = "nome_arquivo_original"),
    @Index(name = "idx_foto_os_tipo_conteudo", columnList = "tipo_conteudo"),
    @Index(name = "idx_foto_os_tamanho", columnList = "tamanho_arquivo"),
    @Index(name = "idx_foto_os_data_upload", columnList = "data_upload"),
    @Index(name = "idx_foto_os_caminho", columnList = "caminho_temporario"),
    @Index(name = "idx_foto_os_foto_url", columnList = "foto_url")
})
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

    @Deprecated
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "foto_base64", columnDefinition = "TEXT")
    private String fotoBase64; // Legado - LAZY: carregado apenas quando getFotoBase64() for chamado

    private String descricao; // Descrição da foto

    private String nomeArquivoOriginal;
    private String tipoConteudo;
    private Long tamanhoArquivo;

    private LocalDateTime dataUpload;
    private String caminhoTemporario;

    @Column(name = "foto_url")
    private String fotoUrl; // URL da imagem no Google Cloud Storage
}