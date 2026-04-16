package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.*;
import com.codagis.nordeste_servicos.model.StatusOS;
import com.codagis.nordeste_servicos.service.AssinaturaOSService;
import com.codagis.nordeste_servicos.service.FotoOSService;
import com.codagis.nordeste_servicos.service.OrdemServicoService;
import com.codagis.nordeste_servicos.service.PdfGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ordens-servico")
public class OrdemServicoController {

    private static final Logger log = LoggerFactory.getLogger(OrdemServicoController.class);

    @Autowired
    private OrdemServicoService ordemServicoService;

    @Autowired
    private PdfGenerationService pdfGenerationService;

    @Autowired
    private FotoOSService fotoOSService;

    @Autowired
    private AssinaturaOSService assinaturaOSService;


    @GetMapping
    public ResponseEntity<List<OrdemServicoResponseDTO>> getAllOrdensServico(
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) StatusOS status,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<OrdemServicoResponseDTO> ordens = ordemServicoService.findAllOrdensServico(tecnicoId, clienteId, status, searchTerm, page, size);
        return ResponseEntity.ok(ordens);
    }

    @GetMapping("/paged")
    public ResponseEntity<OrdemServicoPageResponseDTO> getOrdensServicoPage(
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) StatusOS status,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        OrdemServicoPageResponseDTO response = ordemServicoService.findOrdensServicoPage(tecnicoId, clienteId, status, searchTerm, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<OsDashboardStatsDTO> getDashboardStats() {
        OsDashboardStatsDTO stats = ordemServicoService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> getOrdemServicoById(@PathVariable Long id) {
        OrdemServicoResponseDTO ordem = ordemServicoService.findOrdemServicoById(id);
        return ResponseEntity.ok(ordem);
    }

    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> createOrdemServico(@RequestBody OrdemServicoRequestDTO ordemServicoRequestDTO) {
        OrdemServicoResponseDTO savedOrdemServico = ordemServicoService.createOrdemServico(ordemServicoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrdemServico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> updateOrdemServico(@PathVariable Long id, @RequestBody OrdemServicoRequestDTO ordemServicoRequestDTO) {
        OrdemServicoResponseDTO updatedOrdemServico = ordemServicoService.updateOrdemServico(id, ordemServicoRequestDTO);
        return ResponseEntity.ok(updatedOrdemServico);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrdemServico(@PathVariable Long id) {
        ordemServicoService.deleteOrdemServico(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateOrdemServicoStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest statusUpdateRequest) {
        ordemServicoService.updateOrdemServicoStatus(id, statusUpdateRequest.getStatus());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/next-number")
    public ResponseEntity<String> getNextOrdemServicoNumber() {
        String nextNumber = ordemServicoService.getNextOsNumber();
        if (nextNumber != null) {
            return ResponseEntity.ok(nextNumber);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Não foi possível gerar o próximo número da OS.");
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateOsPdf(@PathVariable Long id) {
        try {
            ordemServicoService.updateDataHoraEmissao(id);

            OrdemServicoResponseDTO osData = ordemServicoService.findOrdemServicoById(id);
            if (osData == null) {
                return ResponseEntity.notFound().build();
            }

            log.debug("Data de Emissão para o PDF: {}", osData.getDataHoraEmissao());

            List<FotoOSResponseDTO> fotos = fotoOSService.findFotosByOrdemServicoId(id, true);
            osData.setFotos(fotos);

            Optional<AssinaturaOSResponseDTO> assinatura = assinaturaOSService.findAssinaturaByOrdemServicoId(id);
            assinatura.ifPresent(osData::setAssinatura);

            byte[] pdfBytes = pdfGenerationService.generateOsReportPdf(osData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "relatorio_os_" + osData.getNumeroOS() + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Erro ao gerar PDF da OS. osId={} err={}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}




