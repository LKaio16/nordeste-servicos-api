package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.OrdemServicoRequestDTO;
import com.codagis.nordeste_servicos.dto.OrdemServicoResponseDTO;
import com.codagis.nordeste_servicos.model.StatusOS;
import com.codagis.nordeste_servicos.service.OrdemServicoService;
import com.codagis.nordeste_servicos.service.PdfGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordens-servico")
public class OrdemServicoController {

    @Autowired
    private OrdemServicoService ordemServicoService;

    @Autowired
    private PdfGenerationService pdfGenerationService; // Injetar o serviço de PDF

    @GetMapping
    public ResponseEntity<List<OrdemServicoResponseDTO>> getAllOrdensServico(
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) StatusOS status) {

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

        List<OrdemServicoResponseDTO> ordens = ordemServicoService.findAllOrdensServico();
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

    // TODO: Adicionar endpoints específicos para gerenciar entidades relacionadas da OS
    // Ex:
    // POST /api/ordens-servico/{id}/registros-tempo
    // GET /api/ordens-servico/{id}/registros-tempo
    // POST /api/ordens-servico/{id}/pecas-utilizadas
    // GET /api/ordens-servico/{id}/pecas-utilizadas
    // POST /api/ordens-servico/{id}/fotos
    // GET /api/ordens-servico/{id}/fotos
    // PUT /api/ordens-servico/{id}/assinatura (ou POST)

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
            // 1. Obter os dados da Ordem de Serviço
            OrdemServicoResponseDTO osData = ordemServicoService.findOrdemServicoById(id);
            if (osData == null) {
                return ResponseEntity.notFound().build();
            }

            // 2. Gerar o PDF usando o serviço
            byte[] pdfBytes = pdfGenerationService.generateOsReportPdf(osData);

            // 3. Configurar os cabeçalhos da resposta para download de PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Define o nome do arquivo quando o usuário for baixar
            headers.setContentDispositionFormData("filename", "relatorio_os_" + osData.getNumeroOS() + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            // Logar o erro para depuração
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}




