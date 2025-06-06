package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.ClienteRequestDTO;
import com.codagis.nordeste_servicos.dto.ClienteResponseDTO;
import com.codagis.nordeste_servicos.model.Cliente;
import com.codagis.nordeste_servicos.repository.ClienteRepository;
import org.apache.velocity.exception.ResourceNotFoundException; // Mantenha se usado em outros lugares, senão remova
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException; // Usar exceção mais apropriada

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
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id)); // Exceção mais semântica
        return convertToDTO(cliente);
    }

    public ClienteResponseDTO createCliente(ClienteRequestDTO clienteRequestDTO) {
        Cliente cliente = convertToEntity(clienteRequestDTO);
        Cliente savedCliente = clienteRepository.save(cliente);
        return convertToDTO(savedCliente);
    }

    public ClienteResponseDTO updateCliente(Long id, ClienteRequestDTO clienteRequestDTO) {
        Cliente existingCliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        // *** CORREÇÃO: Atualiza todos os campos do cliente existente com os dados do DTO ***
        existingCliente.setTipoCliente(clienteRequestDTO.getTipoCliente());
        existingCliente.setNomeCompleto(clienteRequestDTO.getNomeCompleto());
        existingCliente.setCpfCnpj(clienteRequestDTO.getCpfCnpj());
        existingCliente.setEmail(clienteRequestDTO.getEmail());
        existingCliente.setTelefonePrincipal(clienteRequestDTO.getTelefonePrincipal());
        existingCliente.setTelefoneAdicional(clienteRequestDTO.getTelefoneAdicional());
        existingCliente.setCep(clienteRequestDTO.getCep());
        existingCliente.setRua(clienteRequestDTO.getRua());
        existingCliente.setNumero(clienteRequestDTO.getNumero());
        existingCliente.setComplemento(clienteRequestDTO.getComplemento());
        existingCliente.setBairro(clienteRequestDTO.getBairro());
        existingCliente.setCidade(clienteRequestDTO.getCidade());
        existingCliente.setEstado(clienteRequestDTO.getEstado());

        Cliente updatedCliente = clienteRepository.save(existingCliente);
        return convertToDTO(updatedCliente);
    }

    public void deleteCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente não encontrado com ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    // *** CORREÇÃO: Atualiza convertToDTO para incluir todos os campos ***
    private ClienteResponseDTO convertToDTO(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setTipoCliente(cliente.getTipoCliente());
        dto.setNomeCompleto(cliente.getNomeCompleto());
        dto.setCpfCnpj(cliente.getCpfCnpj());
        dto.setEmail(cliente.getEmail());
        dto.setTelefonePrincipal(cliente.getTelefonePrincipal());
        dto.setTelefoneAdicional(cliente.getTelefoneAdicional());
        dto.setCep(cliente.getCep());
        dto.setRua(cliente.getRua());
        dto.setNumero(cliente.getNumero());
        dto.setComplemento(cliente.getComplemento());
        dto.setBairro(cliente.getBairro());
        dto.setCidade(cliente.getCidade());
        dto.setEstado(cliente.getEstado());
        return dto;
    }

    // *** CORREÇÃO: Atualiza convertToEntity para incluir todos os campos ***
    private Cliente convertToEntity(ClienteRequestDTO clienteRequestDTO) {
        Cliente cliente = new Cliente();
        cliente.setTipoCliente(clienteRequestDTO.getTipoCliente());
        cliente.setNomeCompleto(clienteRequestDTO.getNomeCompleto());
        cliente.setCpfCnpj(clienteRequestDTO.getCpfCnpj());
        cliente.setEmail(clienteRequestDTO.getEmail());
        cliente.setTelefonePrincipal(clienteRequestDTO.getTelefonePrincipal());
        cliente.setTelefoneAdicional(clienteRequestDTO.getTelefoneAdicional());
        cliente.setCep(clienteRequestDTO.getCep());
        cliente.setRua(clienteRequestDTO.getRua());
        cliente.setNumero(clienteRequestDTO.getNumero());
        cliente.setComplemento(clienteRequestDTO.getComplemento());
        cliente.setBairro(clienteRequestDTO.getBairro());
        cliente.setCidade(clienteRequestDTO.getCidade());
        cliente.setEstado(clienteRequestDTO.getEstado());
        return cliente;
    }
}

