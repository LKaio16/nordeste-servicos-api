package com.codagis.nordeste_servicos.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/internal/temp-images")
public class TempImageController {

    @GetMapping("/{filename:.+}")
    public ResponseEntity<InputStreamResource> getTempImage(@PathVariable String filename) {
        try {
            // Constrói o caminho para o arquivo dentro do diretório temporário do sistema
            Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), filename);
            File file = filePath.toFile();

            if (file.exists()) {
                InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}