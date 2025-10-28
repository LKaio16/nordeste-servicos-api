package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.OrcamentoRequestDTO;
import com.codagis.nordeste_servicos.dto.OrcamentoResponseDTO;
import com.codagis.nordeste_servicos.model.StatusOrcamento;
import com.codagis.nordeste_servicos.service.OrcamentoService;
import com.codagis.nordeste_servicos.service.PdfGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections; // Importar Collections
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orcamentos")
public class OrcamentoController {

    @Autowired
    private OrcamentoService orçamentoService;

    @Autowired
    private OrcamentoService orcamentoService;

    @Autowired
    private PdfGenerationService pdfGenerationService;

    @GetMapping
    public ResponseEntity<List<OrcamentoResponseDTO>> getAllOrcamentos(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) StatusOrcamento status,
            @RequestParam(required = false) Long ordemServicoOrigemId,
            @RequestParam(required = false) String searchTerm) {
        List<OrcamentoResponseDTO> orçamentos = orçamentoService.findAllOrcamentos(clienteId, status, ordemServicoOrigemId, searchTerm);
        return ResponseEntity.ok(orçamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrcamentoResponseDTO> getOrcamentoById(@PathVariable Long id) {
        OrcamentoResponseDTO orçamento = orçamentoService.findOrcamentoById(id);
        return ResponseEntity.ok(orçamento);
    }


    @PostMapping
    public ResponseEntity<OrcamentoResponseDTO> createOrcamento(@RequestBody OrcamentoRequestDTO orçamentoRequestDTO) {
        // TODO: Implementar validação de segurança (quem pode criar orçamento? Admin? Técnico?)
        OrcamentoResponseDTO savedOrcamento = orçamentoService.createOrcamento(orçamentoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrcamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrcamentoResponseDTO> updateOrcamento(@PathVariable Long id, @RequestBody OrcamentoRequestDTO orçamentoRequestDTO) {
        // TODO: Implementar validação de segurança (quem pode atualizar orçamento?)
        // TODO: Considerar quais campos podem ser atualizados dependendo do status do orçamento
        OrcamentoResponseDTO updatedOrcamento = orçamentoService.updateOrcamento(id, orçamentoRequestDTO);
        return ResponseEntity.ok(updatedOrcamento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrcamento(@PathVariable Long id) {
        // TODO: Implementar validação de segurança (quem pode deletar orçamento?)
        orçamentoService.deleteOrcamento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateOrcamentoPdf(@PathVariable Long id) {
        try {

            // 1. Obter os dados do Orçamento
            OrcamentoResponseDTO orcamentoData = orcamentoService.findOrcamentoById(id);
            if (orcamentoData == null) {
                return ResponseEntity.notFound().build();
            }

            // 2. Gerar o PDF usando o serviço
            byte[] pdfBytes = pdfGenerationService.generateOrcamentoReportPdf(orcamentoData);

            // 3. Configurar os cabeçalhos da resposta para download de PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "Orçamento_" + orcamentoData.getNumeroOrcamento() + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // TODO: Adicionar endpoints específicos para gerenciar os ItemOrcamento relacionados
    // Ex:
    // POST /api/orcamentos/{id}/itens
    // GET /api/orcamentos/{id}/itens
    // PUT /api/orcamentos/{id}/itens/{itemId}
    // DELETE /api/orcamentos/{id}/itens/{itemId}

    // TODO: Adicionar endpoint para gerar o PDF do orçamento
    // Ex: GET /api/orcamentos/{id}/pdf
}