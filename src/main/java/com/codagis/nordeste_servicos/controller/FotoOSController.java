package com.codagis.nordeste_servicos.controller;

import com.codagis.nordeste_servicos.dto.FotoOSResponseDTO;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.service.FotoOSService;
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
import java.util.List;

@RestController
@RequestMapping("/api/ordens-servico/{osId}/fotos")
public class FotoOSController {

    @Autowired
    private FotoOSService fotoOSService;

    @GetMapping
    public ResponseEntity<List<FotoOSResponseDTO>> getFotosByOrdemServico(@PathVariable Long osId) {
        List<FotoOSResponseDTO> fotos = fotoOSService.findFotosByOrdemServicoId(osId);
        return ResponseEntity.ok(fotos);
    }

     @GetMapping("/{id}")
     public ResponseEntity<FotoOSResponseDTO> getFotoById(@PathVariable Long osId, @PathVariable Long id) {
         FotoOSResponseDTO foto = fotoOSService.findFotoById(id);
          // TODO: Opcional: Adicionar verificação se foto.getOrdemServicoId() == osId
         return ResponseEntity.ok(foto);
     }

    // Endpoint para fazer upload de uma foto
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Indica que aceita multipart/form-data
    public ResponseEntity<FotoOSResponseDTO> uploadFoto(@PathVariable Long osId,
                                                        @RequestParam("file") MultipartFile file) { // Recebe o arquivo
        // TODO: Validar que a OS existe e que o usuário logado tem permissão para adicionar fotos a ela
        // TODO: Opcional: Receber outros campos via @RequestParam se o FotoOSUploadRequestDTO for usado
        FotoOSResponseDTO savedFoto = fotoOSService.saveFoto(osId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFoto);
    }

     // Endpoint para baixar/visualizar a foto
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFoto(@PathVariable Long osId, @PathVariable Long id) {
        // TODO: Opcional: Adicionar verificação se a foto com ID {id} pertence à OS {osId}
        // TODO: Validar permissão para baixar a foto

        Path filePath = fotoOSService.loadFotoAsResource(id);
        try {
             Resource resource = new UrlResource(filePath.toUri());

             if (resource.exists() || resource.isReadable()) {
                 String contentType = fotoOSService.findFotoById(id).getTipoConteudo(); // Obter o tipo de conteúdo salvo
                 String nomeArquivo = fotoOSService.findFotoById(id).getNomeArquivoOriginal(); // Obter nome original

                 return ResponseEntity.ok()
                         .contentType(MediaType.parseMediaType(contentType)) // Define o Content-Type
                         .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"") // Sugere download
                         .body(resource);
             } else {
                 throw new ResourceNotFoundException("Arquivo da foto não encontrado ou inacessível.");
             }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Erro ao carregar o arquivo da foto: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoto(@PathVariable Long osId, @PathVariable Long id) {
        // TODO: Opcional: Adicionar verificação se a foto com ID {id} pertence à OS {osId}
        // TODO: Validar permissão para deletar a foto
        fotoOSService.deleteFoto(id);
        return ResponseEntity.noContent().build();
    }
}