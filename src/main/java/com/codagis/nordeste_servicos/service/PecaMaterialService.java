package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.PecaMaterialRequestDTO;
import com.codagis.nordeste_servicos.dto.PecaMaterialResponseDTO;
import com.codagis.nordeste_servicos.exception.BusinessException;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.PecaMaterial;
import com.codagis.nordeste_servicos.repository.PecaMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PecaMaterialService {

    @Autowired
    private PecaMaterialRepository pecaMaterialRepository;

    public List<PecaMaterialResponseDTO> findAllPecasMateriais() {
        List<PecaMaterial> pecasMateriais = pecaMaterialRepository.findAll();
        return pecasMateriais.stream()
                             .map(this::convertToDTO)
                             .collect(Collectors.toList());
    }

    public PecaMaterialResponseDTO findPecaMaterialById(Long id) {
        PecaMaterial pecaMaterial = pecaMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Peça/Material não encontrado com ID: " + id));
        return convertToDTO(pecaMaterial);
    }

    public PecaMaterialResponseDTO createPecaMaterial(PecaMaterialRequestDTO pecaMaterialRequestDTO) {

        PecaMaterial pecaMaterial = convertToEntity(pecaMaterialRequestDTO);
        PecaMaterial savedPecaMaterial = pecaMaterialRepository.save(pecaMaterial);
        return convertToDTO(savedPecaMaterial);
    }

    public PecaMaterialResponseDTO updatePecaMaterial(Long id, PecaMaterialRequestDTO pecaMaterialRequestDTO) {
        PecaMaterial existingPecaMaterial = pecaMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Peça/Material não encontrado com ID: " + id));

        existingPecaMaterial.setCodigo(pecaMaterialRequestDTO.getCodigo());
        existingPecaMaterial.setDescricao(pecaMaterialRequestDTO.getDescricao());
        existingPecaMaterial.setPreco(pecaMaterialRequestDTO.getPreco());
        existingPecaMaterial.setEstoque(pecaMaterialRequestDTO.getEstoque());

        PecaMaterial updatedPecaMaterial = pecaMaterialRepository.save(existingPecaMaterial);
        return convertToDTO(updatedPecaMaterial);
    }

    @Transactional
    public void deletePecaMaterial(Long id) {
        if (!pecaMaterialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Peça/Material não encontrado com ID: " + id);
        }
        try {
            pecaMaterialRepository.deleteById(id);
            pecaMaterialRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Não é possível excluir esta Peça/Material, pois está em uso em um Orçamento ou Ordem de Serviço.");
        }
    }

    private PecaMaterialResponseDTO convertToDTO(PecaMaterial pecaMaterial) {
        PecaMaterialResponseDTO dto = new PecaMaterialResponseDTO();
        dto.setId(pecaMaterial.getId());
        dto.setCodigo(pecaMaterial.getCodigo());
        dto.setDescricao(pecaMaterial.getDescricao());
        dto.setPreco(pecaMaterial.getPreco());
        dto.setEstoque(pecaMaterial.getEstoque());
        return dto;
    }

    private PecaMaterial convertToEntity(PecaMaterialRequestDTO pecaMaterialRequestDTO) {
        PecaMaterial pecaMaterial = new PecaMaterial();
        pecaMaterial.setCodigo(pecaMaterialRequestDTO.getCodigo());
        pecaMaterial.setDescricao(pecaMaterialRequestDTO.getDescricao());
        pecaMaterial.setPreco(pecaMaterialRequestDTO.getPreco());
        pecaMaterial.setEstoque(pecaMaterialRequestDTO.getEstoque());
        return pecaMaterial;
    }
}