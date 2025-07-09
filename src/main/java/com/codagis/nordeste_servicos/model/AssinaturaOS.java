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