package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.ReciboRequestDTO;
import com.codagis.nordeste_servicos.dto.ReciboResponseDTO;
import com.codagis.nordeste_servicos.service.PdfGenerationService;
import com.codagis.nordeste_servicos.service.ReciboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recibos")
public class ReciboController {

    @Autowired
    private PdfGenerationService pdfGenerationService;

    @Autowired
    private ReciboService reciboService;

    @GetMapping
    public ResponseEntity<List<ReciboResponseDTO>> getAllRecibos() {
        List<ReciboResponseDTO> recibos = reciboService.findAllRecibos();
        return ResponseEntity.ok(recibos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReciboResponseDTO> getReciboById(@PathVariable Long id) {
        ReciboResponseDTO recibo = reciboService.findReciboById(id);
        return ResponseEntity.ok(recibo);
    }

    @PostMapping
    public ResponseEntity<ReciboResponseDTO> createRecibo(@RequestBody ReciboRequestDTO reciboRequestDTO) {
        // Validar dados
        if (reciboRequestDTO.getValor() == null || reciboRequestDTO.getValor() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (reciboRequestDTO.getCliente() == null || reciboRequestDTO.getCliente().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (reciboRequestDTO.getReferenteA() == null || reciboRequestDTO.getReferenteA().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ReciboResponseDTO savedRecibo = reciboService.createRecibo(reciboRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecibo);
    }

    @PostMapping("/pdf")
    public ResponseEntity<byte[]> generateReciboPdf(@RequestBody ReciboRequestDTO reciboRequestDTO) {
        try {
            // Validar dados
            if (reciboRequestDTO.getValor() == null || reciboRequestDTO.getValor() <= 0) {
                return ResponseEntity.badRequest().build();
            }
            if (reciboRequestDTO.getCliente() == null || reciboRequestDTO.getCliente().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (reciboRequestDTO.getReferenteA() == null || reciboRequestDTO.getReferenteA().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Gerar o PDF usando o serviço
            byte[] pdfBytes = pdfGenerationService.generateReciboPdf(reciboRequestDTO);

            // Configurar os cabeçalhos da resposta para download de PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "Recibo_" + System.currentTimeMillis() + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateReciboPdfById(@PathVariable Long id) {
        try {
            ReciboResponseDTO recibo = reciboService.findReciboById(id);
            
            ReciboRequestDTO reciboRequestDTO = new ReciboRequestDTO();
            reciboRequestDTO.setValor(recibo.getValor());
            reciboRequestDTO.setCliente(recibo.getCliente());
            reciboRequestDTO.setReferenteA(recibo.getReferenteA());

            byte[] pdfBytes = pdfGenerationService.generateReciboPdf(reciboRequestDTO);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "Recibo_" + recibo.getNumeroRecibo() + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecibo(@PathVariable Long id) {
        try {
            reciboService.deleteRecibo(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

