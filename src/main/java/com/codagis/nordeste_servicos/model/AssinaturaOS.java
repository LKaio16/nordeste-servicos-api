package com.codagis.nordeste_servicos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "assinaturaos", indexes = {
    @Index(name = "idx_assin_ordem_servico", columnList = "ordem_servico_id"),
    @Index(name = "idx_assin_nome_cliente", columnList = "nome_cliente_responsavel"),
    @Index(name = "idx_assin_doc_cliente", columnList = "documento_cliente_responsavel"),
    @Index(name = "idx_assin_nome_tecnico", columnList = "nome_tecnico_responsavel"),
    @Index(name = "idx_assin_data_hora", columnList = "data_hora_coleta")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssinaturaOS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ordem_servico_id", nullable = false, unique = true)
    private OrdemServico ordemServico;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String assinaturaClienteBase64;

    private String nomeClienteResponsavel;
    private String documentoClienteResponsavel;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String assinaturaTecnicoBase64;

    private String nomeTecnicoResponsavel;

    private LocalDateTime dataHoraColeta;
}