package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.EquipamentoRequestDTO;
import com.codagis.nordeste_servicos.dto.EquipamentoResponseDTO;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.Cliente;
import com.codagis.nordeste_servicos.model.Equipamento;
import com.codagis.nordeste_servicos.repository.ClienteRepository;
import com.codagis.nordeste_servicos.repository.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipamentoService {

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private ClienteRepository clienteRepository; // Precisamos do repositório de Cliente

    public List<EquipamentoResponseDTO> findAllEquipamentos() {
        List<Equipamento> equipamentos = equipamentoRepository.findAll();
        return equipamentos.stream()
                           .map(this::convertToDTO)
                           .collect(Collectors.toList());
    }

    // Método para listar equipamentos de um cliente específico
    public List<EquipamentoResponseDTO> findEquipamentosByClienteId(Long clienteId) {
         if (!clienteRepository.existsById(clienteId)) {
             throw new ResourceNotFoundException("Cliente não encontrado com ID: " + clienteId);
         }
        List<Equipamento> equipamentos = equipamentoRepository.findByClienteId(clienteId);
        return equipamentos.stream()
                           .map(this::convertToDTO)
                           .collect(Collectors.toList());
    }


    public EquipamentoResponseDTO findEquipamentoById(Long id) {
        Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipamento não encontrado com ID: " + id));
        return convertToDTO(equipamento);
    }

    public EquipamentoResponseDTO createEquipamento(EquipamentoRequestDTO equipamentoRequestDTO) {
        // Verifica se o cliente associado existe
        Cliente cliente = clienteRepository.findById(equipamentoRequestDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + equipamentoRequestDTO.getClienteId()));

        Equipamento equipamento = convertToEntity(equipamentoRequestDTO);
        equipamento.setCliente(cliente); // Associa o cliente encontrado

        Equipamento savedEquipamento = equipamentoRepository.save(equipamento);
        return convertToDTO(savedEquipamento);
    }

    public EquipamentoResponseDTO updateEquipamento(Long id, EquipamentoRequestDTO equipamentoRequestDTO) {
        Equipamento existingEquipamento = equipamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipamento não encontrado com ID: " + id));

        // Verifica se o novo cliente associado (se mudou) existe
         Cliente cliente = clienteRepository.findById(equipamentoRequestDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + equipamentoRequestDTO.getClienteId()));


        existingEquipamento.setTipo(equipamentoRequestDTO.getTipo());
        existingEquipamento.setMarcaModelo(equipamentoRequestDTO.getMarcaModelo());
        existingEquipamento.setNumeroSerieChassi(equipamentoRequestDTO.getNumeroSerieChassi());
        existingEquipamento.setHorimetro(equipamentoRequestDTO.getHorimetro());
        existingEquipamento.setCliente(cliente); // Atualiza o cliente associado

        Equipamento updatedEquipamento = equipamentoRepository.save(existingEquipamento);
        return convertToDTO(updatedEquipamento);
    }

    public void deleteEquipamento(Long id) {
        if (!equipamentoRepository.existsById(id)) {
             throw new ResourceNotFoundException("Equipamento não encontrado com ID: " + id);
        }
        equipamentoRepository.deleteById(id);
    }

    // Método utilitário para converter Entidade para DTO
    private EquipamentoResponseDTO convertToDTO(Equipamento equipamento) {
        EquipamentoResponseDTO dto = new EquipamentoResponseDTO();
        dto.setId(equipamento.getId());
        dto.setTipo(equipamento.getTipo());
        dto.setMarcaModelo(equipamento.getMarcaModelo());
        dto.setNumeroSerieChassi(equipamento.getNumeroSerieChassi());
        dto.setHorimetro(equipamento.getHorimetro());
        dto.setClienteId(equipamento.getCliente().getId()); // Inclui o ID do cliente
        return dto;
    }

    // Método utilitário para converter DTO para Entidade
    private Equipamento convertToEntity(EquipamentoRequestDTO equipamentoRequestDTO) {
        Equipamento equipamento = new Equipamento();
        equipamento.setTipo(equipamentoRequestDTO.getTipo());
        equipamento.setMarcaModelo(equipamentoRequestDTO.getMarcaModelo());
        equipamento.setNumeroSerieChassi(equipamentoRequestDTO.getNumeroSerieChassi());
        equipamento.setHorimetro(equipamentoRequestDTO.getHorimetro());
        // O Cliente em si é definido no serviço após buscar pelo ID
        return equipamento;
    }
}