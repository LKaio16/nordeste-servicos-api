package com.codagis.nordeste_servicos.service;


import com.codagis.nordeste_servicos.dto.ClienteRequestDTO;
import com.codagis.nordeste_servicos.dto.ClienteResponseDTO;
import com.codagis.nordeste_servicos.model.Cliente;
import com.codagis.nordeste_servicos.repository.ClienteRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<ClienteResponseDTO> findAllClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                       .map(this::convertToDTO) // Mapeia cada Cliente para ClienteResponseDTO
                       .collect(Collectors.toList());
    }

    public ClienteResponseDTO findClienteById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));
        return convertToDTO(cliente);
    }

    public ClienteResponseDTO createCliente(ClienteRequestDTO clienteRequestDTO) {
        Cliente cliente = convertToEntity(clienteRequestDTO);
        Cliente savedCliente = clienteRepository.save(cliente);
        return convertToDTO(savedCliente);
    }

    public ClienteResponseDTO updateCliente(Long id, ClienteRequestDTO clienteRequestDTO) {
        Cliente existingCliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        // Atualiza os campos do cliente existente com os dados do DTO
        existingCliente.setNomeRazaoSocial(clienteRequestDTO.getNomeRazaoSocial());
        existingCliente.setEndereco(clienteRequestDTO.getEndereco());
        existingCliente.setTelefone(clienteRequestDTO.getTelefone());
        existingCliente.setEmail(clienteRequestDTO.getEmail());
        existingCliente.setCnpjCpf(clienteRequestDTO.getCnpjCpf());

        Cliente updatedCliente = clienteRepository.save(existingCliente);
        return convertToDTO(updatedCliente);
    }

    public void deleteCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
             throw new ResourceNotFoundException("Cliente não encontrado com ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private ClienteResponseDTO convertToDTO(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setNomeRazaoSocial(cliente.getNomeRazaoSocial());
        dto.setEndereco(cliente.getEndereco());
        dto.setTelefone(cliente.getTelefone());
        dto.setEmail(cliente.getEmail());
        dto.setCnpjCpf(cliente.getCnpjCpf());
        return dto;
    }


    private Cliente convertToEntity(ClienteRequestDTO clienteRequestDTO) {
        Cliente cliente = new Cliente();
        cliente.setNomeRazaoSocial(clienteRequestDTO.getNomeRazaoSocial());
        cliente.setEndereco(clienteRequestDTO.getEndereco());
        cliente.setTelefone(clienteRequestDTO.getTelefone());
        cliente.setEmail(clienteRequestDTO.getEmail());
        cliente.setCnpjCpf(clienteRequestDTO.getCnpjCpf());
        return cliente;
    }
}