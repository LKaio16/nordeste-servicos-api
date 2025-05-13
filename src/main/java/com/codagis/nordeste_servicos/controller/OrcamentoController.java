package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.OrcamentoRequestDTO;
import com.codagis.nordeste_servicos.dto.OrcamentoResponseDTO;
import com.codagis.nordeste_servicos.model.StatusOrcamento;
import com.codagis.nordeste_servicos.service.OrcamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public ResponseEntity<List<OrcamentoResponseDTO>> getAllOrcamentos(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) StatusOrcamento status,
            @RequestParam(required = false) Long ordemServicoOrigemId) {

        if (clienteId != null) {
            List<OrcamentoResponseDTO> orçamentos = orçamentoService.findOrcamentosByClienteId(clienteId);
            return ResponseEntity.ok(orçamentos);
        }
        if (status != null) {
            List<OrcamentoResponseDTO> orçamentos = orçamentoService.findOrcamentosByStatus(status);
            return ResponseEntity.ok(orçamentos);
        }
        if (ordemServicoOrigemId != null) {
            Optional<OrcamentoResponseDTO> orçamento = orçamentoService.findOrcamentoByOrdemServicoId(ordemServicoOrigemId);
            // CORRIGIDO AQUI: Retorna uma lista contendo o orçamento encontrado, ou uma lista vazia se não encontrado.
            return orçamento.map(orc -> ResponseEntity.ok(Collections.singletonList(orc)))
                    .orElseGet(() -> ResponseEntity.ok(Collections.emptyList()));
        }

        List<OrcamentoResponseDTO> orçamentos = orçamentoService.findAllOrcamentos();
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

    // TODO: Adicionar endpoints específicos para gerenciar os ItemOrcamento relacionados
    // Ex:
    // POST /api/orcamentos/{id}/itens
    // GET /api/orcamentos/{id}/itens
    // PUT /api/orcamentos/{id}/itens/{itemId}
    // DELETE /api/orcamentos/{id}/itens/{itemId}

    // TODO: Adicionar endpoint para gerar o PDF do orçamento
    // Ex: GET /api/orcamentos/{id}/pdf
}