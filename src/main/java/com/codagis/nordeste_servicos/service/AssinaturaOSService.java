package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.AssinaturaOSRequestDTO;
import com.codagis.nordeste_servicos.dto.AssinaturaOSResponseDTO;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.AssinaturaOS;
import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.repository.AssinaturaOSRepository;
import com.codagis.nordeste_servicos.repository.OrdemServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AssinaturaOSService {

    @Autowired
    private AssinaturaOSRepository assinaturaOSRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Transactional(readOnly = true)
    public Optional<AssinaturaOSResponseDTO> findAssinaturaByOrdemServicoId(Long ordemServicoId) {
        return assinaturaOSRepository.findByOrdemServicoId(ordemServicoId).map(this::convertToDTO);
    }

    @Transactional
    public AssinaturaOSResponseDTO saveOrUpdateAssinatura(Long ordemServicoId, AssinaturaOSRequestDTO requestDTO) {
        OrdemServico ordemServico = ordemServicoRepository.findById(ordemServicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + ordemServicoId));

        AssinaturaOS assinatura = assinaturaOSRepository.findByOrdemServicoId(ordemServicoId)
                .orElse(new AssinaturaOS());

        assinatura.setOrdemServico(ordemServico);
        assinatura.setAssinaturaClienteBase64(requestDTO.getAssinaturaClienteBase64());
        assinatura.setNomeClienteResponsavel(requestDTO.getNomeClienteResponsavel());
        assinatura.setDocumentoClienteResponsavel(requestDTO.getDocumentoClienteResponsavel());
        assinatura.setAssinaturaTecnicoBase64(requestDTO.getAssinaturaTecnicoBase64());
        assinatura.setNomeTecnicoResponsavel(requestDTO.getNomeTecnicoResponsavel());
        assinatura.setDataHoraColeta(LocalDateTime.now());

        AssinaturaOS savedAssinatura = assinaturaOSRepository.save(assinatura);
        return convertToDTO(savedAssinatura);
    }

    @Transactional
    public void deleteAssinatura(Long osId) {
        assinaturaOSRepository.findByOrdemServicoId(osId).ifPresent(assinaturaOSRepository::delete);
    }

    private AssinaturaOSResponseDTO convertToDTO(AssinaturaOS assinatura) {
        return new AssinaturaOSResponseDTO(
                assinatura.getId(),
                assinatura.getOrdemServico().getId(),
                assinatura.getAssinaturaClienteBase64(),
                assinatura.getNomeClienteResponsavel(),
                assinatura.getDocumentoClienteResponsavel(),
                assinatura.getAssinaturaTecnicoBase64(),
                assinatura.getNomeTecnicoResponsavel(),
                assinatura.getDataHoraColeta()
        );
    }
}