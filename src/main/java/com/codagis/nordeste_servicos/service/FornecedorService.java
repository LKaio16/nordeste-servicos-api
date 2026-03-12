package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.FornecedorRequestDTO;
import com.codagis.nordeste_servicos.dto.FornecedorResponseDTO;
import com.codagis.nordeste_servicos.exception.BusinessException;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.Fornecedor;
import com.codagis.nordeste_servicos.repository.ClienteRepository;
import com.codagis.nordeste_servicos.repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<FornecedorResponseDTO> findAll(String searchTerm, String status) {
        List<Fornecedor> list;
        if (searchTerm != null && !searchTerm.isBlank()) {
            list = fornecedorRepository.findByNomeContainingIgnoreCase(searchTerm.trim());
        } else if (status != null && !status.isBlank()) {
            try {
                list = fornecedorRepository.findByStatus(com.codagis.nordeste_servicos.model.StatusFornecedor.valueOf(status));
            } catch (IllegalArgumentException e) {
                list = fornecedorRepository.findAll();
            }
        } else {
            list = fornecedorRepository.findAll();
        }
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FornecedorResponseDTO findById(Long id) {
        Fornecedor f = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com ID: " + id));
        return toResponse(f);
    }

    @Transactional
    public FornecedorResponseDTO create(FornecedorRequestDTO dto) {
        String cnpj = dto.getCnpj() != null ? dto.getCnpj().trim() : "";
        if (fornecedorRepository.existsByCnpj(cnpj)) {
            throw new BusinessException("CNPJ já cadastrado para outro fornecedor.");
        }
        if (clienteRepository.findByCpfCnpj(cnpj).isPresent()) {
            throw new BusinessException("CNPJ já cadastrado para um cliente.");
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new BusinessException("Email inválido.");
        }

        Fornecedor f = new Fornecedor();
        mapDtoToEntity(dto, f);
        f = fornecedorRepository.save(f);
        return toResponse(f);
    }

    @Transactional
    public FornecedorResponseDTO update(Long id, FornecedorRequestDTO dto) {
        Fornecedor f = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com ID: " + id));
        String cnpj = dto.getCnpj() != null ? dto.getCnpj().trim() : "";
        if (!f.getCnpj().equals(cnpj)) {
            if (fornecedorRepository.existsByCnpj(cnpj)) {
                throw new BusinessException("CNPJ já cadastrado para outro fornecedor.");
            }
            if (clienteRepository.findByCpfCnpj(cnpj).isPresent()) {
                throw new BusinessException("CNPJ já cadastrado para um cliente.");
            }
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new BusinessException("Email inválido.");
        }

        mapDtoToEntity(dto, f);
        f = fornecedorRepository.save(f);
        return toResponse(f);
    }

    @Transactional
    public void delete(Long id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fornecedor não encontrado com ID: " + id);
        }
        fornecedorRepository.deleteById(id);
    }

    private void mapDtoToEntity(FornecedorRequestDTO dto, Fornecedor f) {
        f.setNome(dto.getNome());
        f.setCnpj(dto.getCnpj().trim());
        f.setEmail(dto.getEmail() != null && !dto.getEmail().isBlank() ? dto.getEmail().trim() : null);
        f.setTelefone(dto.getTelefone() != null && !dto.getTelefone().isBlank() ? dto.getTelefone().trim() : null);
        f.setEndereco(dto.getEndereco());
        f.setCidade(dto.getCidade());
        f.setEstado(dto.getEstado());
        f.setStatus(dto.getStatus());
        f.setObservacoes(dto.getObservacoes());
    }

    private FornecedorResponseDTO toResponse(Fornecedor f) {
        FornecedorResponseDTO dto = new FornecedorResponseDTO();
        dto.setId(f.getId());
        dto.setNome(f.getNome());
        dto.setCnpj(f.getCnpj());
        dto.setEmail(f.getEmail());
        dto.setTelefone(f.getTelefone());
        dto.setEndereco(f.getEndereco());
        dto.setCidade(f.getCidade());
        dto.setEstado(f.getEstado());
        dto.setStatus(f.getStatus());
        dto.setObservacoes(f.getObservacoes());
        return dto;
    }
}
