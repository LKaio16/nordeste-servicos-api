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

import java.util.Base64;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FotoOSService {

    @Autowired
    private FotoOSRepository fotoOSRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired(required = false)
    private GoogleCloudStorageService googleCloudStorageService;

    @Transactional(readOnly = true)
    public List<FotoOSResponseDTO> findFotosByOrdemServicoId(Long ordemServicoId) {
        return findFotosByOrdemServicoId(ordemServicoId, true);
    }

    @Transactional(readOnly = true)
    public List<FotoOSResponseDTO> findFotosByOrdemServicoId(Long ordemServicoId, boolean includeBase64) {
        List<FotoOS> fotos = fotoOSRepository.findByOrdemServicoId(ordemServicoId);
        return fotos.stream()
                .map(f -> convertToDTO(f, Objects.isNull(f.getFotoUrl())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FotoOSResponseDTO findFotoById(Long fotoId) {
        FotoOS foto = fotoOSRepository.findById(fotoId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto não encontrada com ID: " + fotoId));
        return convertToDTO(foto, true);
    }

    @Transactional
    public FotoOSResponseDTO saveFoto(Long ordemServicoId, FotoOSUploadRequestDTO requestDTO) {
        OrdemServico ordemServico = ordemServicoRepository.findById(ordemServicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + ordemServicoId));

        if (requestDTO.getFotoBase64() == null || requestDTO.getFotoBase64().isEmpty()) {
            throw new BusinessException("A imagem (string Base64) não pode ser nula ou vazia.");
        }

        if (googleCloudStorageService == null) {
            throw new BusinessException("Google Cloud Storage não configurado. Configure gcloud.enabled=true, GCLOUD_BUCKET e as credenciais (GOOGLE_APPLICATION_CREDENTIALS ou GCLOUD_CREDENTIALS_JSON).");
        }

        byte[] imageBytes = Base64.getDecoder().decode(requestDTO.getFotoBase64());
        String contentType = requestDTO.getTipoConteudo() != null ? requestDTO.getTipoConteudo() : "image/jpeg";
        String fotoUrl = googleCloudStorageService.uploadImage(
                ordemServicoId, imageBytes, contentType, requestDTO.getNomeArquivoOriginal());

        FotoOS fotoOS = new FotoOS();
        fotoOS.setOrdemServico(ordemServico);
        fotoOS.setFotoBase64(null);
        fotoOS.setDescricao(requestDTO.getDescricao());
        fotoOS.setNomeArquivoOriginal(requestDTO.getNomeArquivoOriginal());
        fotoOS.setTipoConteudo(requestDTO.getTipoConteudo());
        fotoOS.setTamanhoArquivo(requestDTO.getTamanhoArquivo());
        fotoOS.setDataUpload(LocalDateTime.now());
        fotoOS.setFotoUrl(fotoUrl);

        FotoOS savedFotoOS = fotoOSRepository.save(fotoOS);
        return convertToDTO(savedFotoOS, false);
    }

    @Transactional
    public void deleteFoto(Long fotoId) {
        FotoOS foto = fotoOSRepository.findById(fotoId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto não encontrada com ID: " + fotoId));

        if (foto.getFotoUrl() != null && googleCloudStorageService != null) {
            try {
                googleCloudStorageService.deleteImage(foto.getFotoUrl());
            } catch (Exception e) {
                // Log e continua - remove do banco mesmo se falhar na nuvem
            }
        }

        fotoOSRepository.deleteById(fotoId);
    }

    private FotoOSResponseDTO convertToDTO(FotoOS fotoOS, boolean includeBase64) {
        // Listagem: não retorna base64 (evita payload gigante). Use GET /fotos/{id} para base64 de legado
        String base64 = includeBase64 ? fotoOS.getFotoBase64() : null;
        return new FotoOSResponseDTO(
                fotoOS.getId(),
                fotoOS.getOrdemServico().getId(),
                base64,
                fotoOS.getDescricao(),
                fotoOS.getNomeArquivoOriginal(),
                fotoOS.getTipoConteudo(),
                fotoOS.getTamanhoArquivo(),
                fotoOS.getDataUpload(),
                fotoOS.getCaminhoTemporario(),
                fotoOS.getFotoUrl()
        );
    }
}