package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.*;
import com.codagis.nordeste_servicos.model.StatusOS;
import com.codagis.nordeste_servicos.service.AssinaturaOSService;
import com.codagis.nordeste_servicos.service.FotoOSService;
import com.codagis.nordeste_servicos.service.OrdemServicoService;
import com.codagis.nordeste_servicos.service.PdfGenerationService;
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

    @Autowired
    private OrdemServicoService ordemServicoService;

    @Autowired
    private PdfGenerationService pdfGenerationService; // Injetar o serviço de PDF

    @Autowired
    private FotoOSService fotoOSService;

    @Autowired
    private AssinaturaOSService assinaturaOSService;


    @GetMapping
    public ResponseEntity<List<OrdemServicoResponseDTO>> getAllOrdensServico(
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) StatusOS status,
            @RequestParam(required = false) String searchTerm) { // Parâmetro adicionado

        if (tecnicoId != null) {
            List<OrdemServicoResponseDTO> ordens = ordemServicoService.findOrdensServicoByTecnicoId(tecnicoId);
            return ResponseEntity.ok(ordens);
        }
        if (clienteId != null) {
            List<OrdemServicoResponseDTO> ordens = ordemServicoService.findOrdensServicoByClienteId(clienteId);
            return ResponseEntity.ok(ordens);
        }
        if (status != null) {
            List<OrdemServicoResponseDTO> ordens = ordemServicoService.findOrdensServicoByStatus(status);
            return ResponseEntity.ok(ordens);
        }

        // Lógica de busca agora é chamada no serviço
        List<OrdemServicoResponseDTO> ordens = ordemServicoService.findAllOrdensServico(searchTerm);
        return ResponseEntity.ok(ordens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> getOrdemServicoById(@PathVariable Long id) {
        OrdemServicoResponseDTO ordem = ordemServicoService.findOrdemServicoById(id);
        return ResponseEntity.ok(ordem);
    }

    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> createOrdemServico(@RequestBody OrdemServicoRequestDTO ordemServicoRequestDTO) {
        // TODO: Implementar validação de segurança para permitir apenas ADMIN criar OS
        OrdemServicoResponseDTO savedOrdemServico = ordemServicoService.createOrdemServico(ordemServicoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrdemServico);
    }

    // Este endpoint de PUT pode ser usado para atualizações gerais (Admin)
    // ou atualizações de execução (Técnico), dependendo da lógica no service e da segurança
    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> updateOrdemServico(@PathVariable Long id, @RequestBody OrdemServicoRequestDTO ordemServicoRequestDTO) {
        // TODO: Implementar validação de segurança baseada no perfil (Admin vs Técnico) e status da OS
        OrdemServicoResponseDTO updatedOrdemServico = ordemServicoService.updateOrdemServico(id, ordemServicoRequestDTO);
        return ResponseEntity.ok(updatedOrdemServico);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrdemServico(@PathVariable Long id) {
        // TODO: Implementar validação de segurança para permitir apenas ADMIN deletar OS
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
        String nextNumber = ordemServicoService.getNextOsNumber(); // Método a ser criado no Service
        if (nextNumber != null) {
            return ResponseEntity.ok(nextNumber);
        } else {
            // Retorna um erro 500 se não conseguir gerar o número
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Não foi possível gerar o próximo número da OS.");
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateOsPdf(@PathVariable Long id) {
        try {
            // 1. Obter os dados da Ordem de Serviço (sem as fotos, como antes)
            OrdemServicoResponseDTO osData = ordemServicoService.findOrdemServicoById(id);
            if (osData == null) {
                return ResponseEntity.notFound().build();
            }


            // 2. Buscar as fotos separadamente usando o FotoOSService
            List<FotoOSResponseDTO> fotos = fotoOSService.findFotosByOrdemServicoId(id);

            osData.setFotos(fotos);

            // 3. (NOVO) Buscar a assinatura associada à OS
            Optional<AssinaturaOSResponseDTO> assinatura = assinaturaOSService.findAssinaturaByOrdemServicoId(id);
            assinatura.ifPresent(osData::setAssinatura); // Adiciona a assinatura ao DTO principal se ela existir


            // 4. Gerar o PDF usando o serviço com os dados agora completos
            byte[] pdfBytes = pdfGenerationService.generateOsReportPdf(osData);

            // 5. Configurar os cabeçalhos da resposta para download de PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "relatorio_os_" + osData.getNumeroOS() + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}




