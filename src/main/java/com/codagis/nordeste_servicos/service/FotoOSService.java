package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.FotoOSResponseDTO;
import com.codagis.nordeste_servicos.dto.FotoOSUploadRequestDTO;
import com.codagis.nordeste_servicos.exception.BusinessException;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.FotoOS;
import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.repository.FotoOSRepository;
import com.codagis.nordeste_servicos.repository.OrdemServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FotoOSService {

    @Autowired
    private FotoOSRepository fotoOSRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Transactional(readOnly = true)
    public List<FotoOSResponseDTO> findFotosByOrdemServicoId(Long ordemServicoId) {
        List<FotoOS> fotos = fotoOSRepository.findByOrdemServicoId(ordemServicoId);
        return fotos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public FotoOSResponseDTO saveFoto(Long ordemServicoId, FotoOSUploadRequestDTO requestDTO) {
        OrdemServico ordemServico = ordemServicoRepository.findById(ordemServicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + ordemServicoId));

        if (requestDTO.getFotoBase64() == null || requestDTO.getFotoBase64().isEmpty()) {
            throw new BusinessException("A imagem (string Base64) não pode ser nula ou vazia.");
        }

        FotoOS fotoOS = new FotoOS();
        fotoOS.setOrdemServico(ordemServico);
        fotoOS.setFotoBase64(requestDTO.getFotoBase64());
        fotoOS.setDescricao(requestDTO.getDescricao());
        fotoOS.setNomeArquivoOriginal(requestDTO.getNomeArquivoOriginal());
        fotoOS.setTipoConteudo(requestDTO.getTipoConteudo());
        fotoOS.setTamanhoArquivo(requestDTO.getTamanhoArquivo());
        fotoOS.setDataUpload(LocalDateTime.now());

        FotoOS savedFotoOS = fotoOSRepository.save(fotoOS);
        return convertToDTO(savedFotoOS);
    }

    @Transactional
    public void deleteFoto(Long fotoId) {
        if (!fotoOSRepository.existsById(fotoId)) {
            throw new ResourceNotFoundException("Foto não encontrada com ID: " + fotoId);
        }
        fotoOSRepository.deleteById(fotoId);
    }

    private FotoOSResponseDTO convertToDTO(FotoOS fotoOS) {
        return new FotoOSResponseDTO(
                fotoOS.getId(),
                fotoOS.getOrdemServico().getId(),
                fotoOS.getFotoBase64(),
                fotoOS.getDescricao(),
                fotoOS.getNomeArquivoOriginal(),
                fotoOS.getTipoConteudo(),
                fotoOS.getTamanhoArquivo(),
                fotoOS.getDataUpload(),
                fotoOS.getCaminhoTemporario()
        );
    }
}