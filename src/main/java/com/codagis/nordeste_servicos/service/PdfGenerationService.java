package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.*;
import com.codagis.nordeste_servicos.util.NumeroExtensoUtil;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

@Service
public class PdfGenerationService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private ItemOrcamentoService itemOrcamentoService;

    private String formatarHorasDecimais(Double horasDecimais) {
        if (horasDecimais == null || horasDecimais < 0) {
            return "0h 00m";
        }
        int horas = horasDecimais.intValue();
        int minutos = (int) Math.round((horasDecimais - horas) * 60);
        return String.format("%dh %02dm", horas, minutos);
    }

    public byte[] generateOsReportPdf(OrdemServicoResponseDTO osData) throws Exception {

        if (osData.getFotos() != null) {
        for (FotoOSResponseDTO foto : osData.getFotos()) {
            if (foto.getFotoUrl() != null) {
                foto.setCaminhoTemporario(foto.getFotoUrl());
            } else if (foto.getFotoBase64() != null && !foto.getFotoBase64().isBlank()) {
                Path tempFile = Files.createTempFile("foto_os_", ".jpg");
                byte[] decodedBytes = Base64.getDecoder().decode(foto.getFotoBase64());
                Files.write(tempFile, decodedBytes);
                String imageUrl = "http://localhost:8080/api/internal/temp-images/" + tempFile.getFileName().toString();
                foto.setCaminhoTemporario(imageUrl);
            }
        }
        }

        Context context = new Context();
        context.setVariable("os", osData);

        double totalHorasDecimal = 0.0;
        if (osData.getRegistrosTempo() != null) {
            totalHorasDecimal = osData.getRegistrosTempo().stream()
                    .filter(rt -> rt.getHorasTrabalhadas() != null)
                    .mapToDouble(RegistroTempoResponseDTO::getHorasTrabalhadas)
                    .sum();
        }

        context.setVariable("tempoTotalFormatado", formatarHorasDecimais(totalHorasDecimal));

        AssinaturaOSResponseDTO assinatura = osData.getAssinatura();
        if (assinatura != null) {

            if (assinatura.getAssinaturaClienteBase64() != null && !assinatura.getAssinaturaClienteBase64().isBlank()) {
                Path tempFileCliente = Files.createTempFile("assinatura_cliente_", ".png");
                byte[] decodedBytes = Base64.getDecoder().decode(assinatura.getAssinaturaClienteBase64());
                Files.write(tempFileCliente, decodedBytes);
                String urlAssinaturaCliente = "http://localhost:8080/api/internal/temp-images/" + tempFileCliente.getFileName().toString();
                context.setVariable("urlAssinaturaCliente", urlAssinaturaCliente);
            }

            if (assinatura.getAssinaturaTecnicoBase64() != null && !assinatura.getAssinaturaTecnicoBase64().isBlank()) {
                Path tempFileTecnico = Files.createTempFile("assinatura_tecnico_", ".png");
                byte[] decodedBytes = Base64.getDecoder().decode(assinatura.getAssinaturaTecnicoBase64());
                Files.write(tempFileTecnico, decodedBytes);
                String urlAssinaturaTecnico = "http://localhost:8080/api/internal/temp-images/" + tempFileTecnico.getFileName().toString();
                context.setVariable("urlAssinaturaTecnico", urlAssinaturaTecnico);
            }
        }

        String htmlContent = templateEngine.process("os-report-template", context);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(htmlContent, "http://localhost:8080");
        builder.toStream(os);
        builder.run();

        if (osData.getFotos() != null) {
        for (FotoOSResponseDTO foto : osData.getFotos()) {
            if (foto.getCaminhoTemporario() != null && foto.getCaminhoTemporario().startsWith("http://localhost")) {
                try {
                    String filename = foto.getCaminhoTemporario().substring(foto.getCaminhoTemporario().lastIndexOf('/') + 1);
                    Path pathToDelete = new File(System.getProperty("java.io.tmpdir"), filename).toPath();
                    Files.deleteIfExists(pathToDelete);
                } catch (Exception e) {

                }
            }
        }
        }

        return os.toByteArray();
    }

    public byte[] generateOrcamentoReportPdf(OrcamentoResponseDTO orcamentoData) throws Exception {
        Context context = new Context();

        context.setLocale(new Locale("pt", "BR"));
        List<ItemOrcamentoResponseDTO> itens = itemOrcamentoService.findItensByOrcamentoId(orcamentoData.getId());
        context.setVariable("orcamento", orcamentoData);
        context.setVariable("itens", itens);
        String htmlContent = templateEngine.process("orcamento-report-template", context);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(htmlContent, null);
        builder.toStream(os);
        builder.run();
        return os.toByteArray();
    }

    public byte[] generateReciboPdf(ReciboRequestDTO reciboData) throws Exception {
        Context context = new Context();
        context.setLocale(new Locale("pt", "BR"));

        String valorFormatado = String.format(Locale.forLanguageTag("pt-BR"), "%.2f", reciboData.getValor());
        valorFormatado = valorFormatado.replace(".", ",");
        String[] partes = valorFormatado.split(",");
        String parteInteira = partes[0];
        String parteDecimal = partes.length > 1 ? partes[1] : "00";

        parteInteira = parteInteira.replaceAll("\\B(?=(\\d{3})+(?!\\d))", ".");
        valorFormatado = "R$ " + parteInteira + "," + parteDecimal;

        String valorExtenso = NumeroExtensoUtil.converterParaExtenso(reciboData.getValor());

        LocalDate hoje = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'DE' MMMM 'DE' yyyy", new Locale("pt", "BR"));
        String dataFormatada = hoje.format(formatter).toUpperCase();

        String referenteA = reciboData.getReferenteA();
        if (referenteA != null && !referenteA.isEmpty()) {
            referenteA = referenteA.substring(0, 1).toUpperCase() +
                        (referenteA.length() > 1 ? referenteA.substring(1) : "");
        }

        context.setVariable("valor", valorFormatado);
        context.setVariable("valorExtenso", valorExtenso);
        context.setVariable("cliente", reciboData.getCliente());
        context.setVariable("referenteA", referenteA);
        context.setVariable("dataFormatada", dataFormatada);

        String htmlContent = templateEngine.process("recibo-template", context);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(htmlContent, null);
        builder.toStream(os);
        builder.run();
        return os.toByteArray();
    }
}