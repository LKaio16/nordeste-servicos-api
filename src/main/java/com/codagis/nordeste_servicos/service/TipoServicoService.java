package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.TipoServicoRequestDTO;
import com.codagis.nordeste_servicos.dto.TipoServicoResponseDTO;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.TipoServico;
import com.codagis.nordeste_servicos.repository.TipoServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoServicoService {

    @Autowired
    private TipoServicoRepository tipoServicoRepository;

    public List<TipoServicoResponseDTO> findAllTiposServico() {
        List<TipoServico> tiposServico = tipoServicoRepository.findAll();
        return tiposServico.stream()
                           .map(this::convertToDTO)
                           .collect(Collectors.toList());
    }

    public TipoServicoResponseDTO findTipoServicoById(Long id) {
        TipoServico tipoServico = tipoServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de Serviço não encontrado com ID: " + id));
        return convertToDTO(tipoServico);
    }

    public TipoServicoResponseDTO createTipoServico(TipoServicoRequestDTO tipoServicoRequestDTO) {
        // Opcional: Verificar se a descrição já existe
        // if (tipoServicoRepository.findByDescricao(tipoServicoRequestDTO.getDescricao()).isPresent()) {
        //     throw new BusinessException("Descrição do tipo de serviço já existe.");
        // }

        TipoServico tipoServico = convertToEntity(tipoServicoRequestDTO);
        TipoServico savedTipoServico = tipoServicoRepository.save(tipoServico);
        return convertToDTO(savedTipoServico);
    }

    public TipoServicoResponseDTO updateTipoServico(Long id, TipoServicoRequestDTO tipoServicoRequestDTO) {
        TipoServico existingTipoServico = tipoServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de Serviço não encontrado com ID: " + id));

        // Opcional: Verificar se a nova descrição já existe em outro tipo
        // Optional<TipoServico> tipoComMesmaDescricao = tipoServicoRepository.findByDescricao(tipoServicoRequestDTO.getDescricao());
        // if (tipoComMesmaDescricao.isPresent() && !tipoComMesmaDescricao.get().getId().equals(id)) {
        //     throw new BusinessException("Descrição do tipo de serviço já existe em outro item.");
        // }

        existingTipoServico.setDescricao(tipoServicoRequestDTO.getDescricao());
        // Opcional: Atualizar precoPadrao

        TipoServico updatedTipoServico = tipoServicoRepository.save(existingTipoServico);
        return convertToDTO(updatedTipoServico);
    }

    public void deleteTipoServico(Long id) {
        if (!tipoServicoRepository.existsById(id)) {
             throw new ResourceNotFoundException("Tipo de Serviço não encontrado com ID: " + id);
        }
         // TODO: Considerar se o tipo de serviço está sendo usado em algum RegistroTempo ou ItemOrcamento
        tipoServicoRepository.deleteById(id);
    }

    private TipoServicoResponseDTO convertToDTO(TipoServico tipoServico) {
        TipoServicoResponseDTO dto = new TipoServicoResponseDTO();
        dto.setId(tipoServico.getId());
        dto.setDescricao(tipoServico.getDescricao());
        // Opcional: Incluir precoPadrao
        return dto;
    }

    private TipoServico convertToEntity(TipoServicoRequestDTO tipoServicoRequestDTO) {
        TipoServico tipoServico = new TipoServico();
        tipoServico.setDescricao(tipoServicoRequestDTO.getDescricao());
        // Opcional: Incluir precoPadrao
        return tipoServico;
    }
}