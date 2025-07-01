package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.ItemOrcamentoResponseDTO;
import com.codagis.nordeste_servicos.dto.OrcamentoResponseDTO;
import com.codagis.nordeste_servicos.dto.OrdemServicoResponseDTO;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

@Service
public class PdfGenerationService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private ItemOrcamentoService itemOrcamentoService;

    public byte[] generateOsReportPdf(OrdemServicoResponseDTO osData) throws Exception {
        Context context = new Context();
        context.setVariable("os", osData); // "os" será o nome da variável no seu template HTML

        // Processar o template HTML com os dados da OS
        String htmlContent = templateEngine.process("os-report-template", context);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(htmlContent, null); // O null é a base URI, pode ser útil para imagens locais
        builder.toStream(os);
        builder.run();

        return os.toByteArray();
    }

    public byte[] generateOrcamentoReportPdf(OrcamentoResponseDTO orcamentoData) throws Exception {
        Context context = new Context();

        // Buscar os itens do orçamento para adicionar ao contexto
        List<ItemOrcamentoResponseDTO> itens = itemOrcamentoService.findItensByOrcamentoId(orcamentoData.getId());

        context.setVariable("orcamento", orcamentoData);
        context.setVariable("itens", itens); // Adiciona a lista de itens

        // Processar o novo template HTML para o orçamento
        String htmlContent = templateEngine.process("orcamento-report-template", context);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(htmlContent, null);
        builder.toStream(os);
        builder.run();

        return os.toByteArray();
    }
}