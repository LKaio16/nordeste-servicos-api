package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.AssinaturaOSResponseDTO;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.service.AssinaturaOSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Optional;

@RestController
@RequestMapping("/api/ordens-servico/{osId}/assinatura")
public class AssinaturaOSController {

    @Autowired
    private AssinaturaOSService assinaturaOSService;

    // Endpoint para obter a assinatura de uma OS (se existir)
    @GetMapping
    public ResponseEntity<AssinaturaOSResponseDTO> getAssinaturaByOrdemServico(@PathVariable Long osId) {
        Optional<AssinaturaOSResponseDTO> assinatura = assinaturaOSService.findAssinaturaByOrdemServicoId(osId);
        return assinatura.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

     // Endpoint para obter uma assinatura específica pelo ID (útil se a chave primária da entidade AssinaturaOS for independente)
     // Embora seja OneToOne, pode ser útil para consistência de URLs ou se o ID não for mapeado diretamente da OS
     @GetMapping("/{id}")
     public ResponseEntity<AssinaturaOSResponseDTO> getAssinaturaById(@PathVariable Long osId, @PathVariable Long id) {
         AssinaturaOSResponseDTO assinatura = assinaturaOSService.findAssinaturaById(id);
          // TODO: Opcional: Adicionar verificação se assinatura.getOrdemServicoId() == osId
         return ResponseEntity.ok(assinatura);
     }


    // Endpoint para SALVAR ou ATUALIZAR a assinatura de uma OS
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssinaturaOSResponseDTO> saveOrUpdateAssinatura(@PathVariable Long osId,
                                                                        @RequestParam("file") MultipartFile file) {
        // TODO: Validar que a OS existe e que o usuário logado tem permissão para adicionar/atualizar a assinatura
        AssinaturaOSResponseDTO savedAssinatura = assinaturaOSService.saveOrUpdateAssinatura(osId, file);
        return ResponseEntity.status(HttpStatus.OK).body(savedAssinatura); // OK pois pode ser update
    }

     // Endpoint para baixar/visualizar a assinatura (usando o ID da entidade AssinaturaOS)
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadAssinatura(@PathVariable Long osId, @PathVariable Long id) {
        // TODO: Opcional: Adicionar verificação se a assinatura com ID {id} pertence à OS {osId}
        // TODO: Validar permissão para baixar a assinatura

        Path filePath = assinaturaOSService.loadAssinaturaAsResource(id);
        try {
             Resource resource = new UrlResource(filePath.toUri());

             if (resource.exists() || resource.isReadable()) {
                  String contentType = assinaturaOSService.findAssinaturaById(id).getTipoConteudo(); // Obter o tipo de conteúdo salvo

                 return ResponseEntity.ok()
                         .contentType(MediaType.parseMediaType(contentType))
                         .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"assinatura_" + id + ".png\"") // 'inline' para exibir no browser
                         .body(resource);
             } else {
                 throw new ResourceNotFoundException("Arquivo da assinatura não encontrado ou inacessível.");
             }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Erro ao carregar o arquivo da assinatura: " + e.getMessage());
        }
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteAssinatura(@PathVariable Long osId) {
        // TODO: Validar permissão para deletar a assinatura
        assinaturaOSService.deleteAssinatura(osId);
        return ResponseEntity.noContent().build();
    }

     // Endpoint para deletar uma assinatura específica pelo ID (se a chave for independente)
    @DeleteMapping("/{id}")
     public ResponseEntity<Void> deleteAssinaturaById(@PathVariable Long osId, @PathVariable Long id) {
         // TODO: Opcional: Adicionar verificação se a assinatura com ID {id} pertence à OS {osId}
         // TODO: Validar permissão para deletar
         assinaturaOSService.deleteAssinatura(id); // Pode precisar de um método delete no service que use o ID da Assinatura
         return ResponseEntity.noContent().build();
     }
}