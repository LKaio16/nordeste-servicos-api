package com.codagis.nordeste_servicos.controller;


import com.codagis.nordeste_servicos.dto.ClienteRequestDTO;
import com.codagis.nordeste_servicos.dto.ClienteResponseDTO;
import com.codagis.nordeste_servicos.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/download")
    public ResponseEntity<Resource> getFile() {
        String filename = "clientes";
        InputStreamResource file = new InputStreamResource(clienteService.load());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> getAllClientes(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String tipoCliente) {
        List<ClienteResponseDTO> clientes = clienteService.findAllClientes(searchTerm, tipoCliente);
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> getClienteById(@PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.findClienteById(id); // O serviço já lança a exceção 404
        return ResponseEntity.ok(cliente);
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> createCliente(@RequestBody ClienteRequestDTO clienteRequestDTO) {
        ClienteResponseDTO savedCliente = clienteService.createCliente(clienteRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> updateCliente(@PathVariable Long id, @RequestBody ClienteRequestDTO clienteRequestDTO) {
        ClienteResponseDTO updatedCliente = clienteService.updateCliente(id, clienteRequestDTO); // O serviço já trata o 404
        return ResponseEntity.ok(updatedCliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id); // O serviço já trata o 404
        return ResponseEntity.noContent().build();
    }
}