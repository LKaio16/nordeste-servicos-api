package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.AssinaturaOSResponseDTO;
import com.codagis.nordeste_servicos.exception.BusinessException;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.AssinaturaOS;
import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.repository.AssinaturaOSRepository;
import com.codagis.nordeste_servicos.repository.OrdemServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Para obter configuração do caminho de upload
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID; // Para gerar nomes de arquivo únicos

@Service
public class AssinaturaOSService {

    @Autowired
    private AssinaturaOSRepository assinaturaOSRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Value("${app.upload.dir:${user.home}/uploads/assinaturas}") // Diretório específico para assinaturas
    private String uploadDirAssinaturas;


    public Optional<AssinaturaOSResponseDTO> findAssinaturaByOrdemServicoId(Long ordemServicoId) {
        Optional<AssinaturaOS> assinatura = assinaturaOSRepository.findByOrdemServicoId(ordemServicoId);
        return assinatura.map(this::convertToDTO);
    }

     public AssinaturaOSResponseDTO findAssinaturaById(Long id) {
         AssinaturaOS assinatura = assinaturaOSRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assinatura não encontrada com ID: " + id));
         return convertToDTO(assinatura);
     }


    // Método para salvar/atualizar a assinatura (arquivo) e o registro no banco
    // Usamos PUT pois é um recurso 1:1 e a ideia é criar/substituir
    public AssinaturaOSResponseDTO saveOrUpdateAssinatura(Long ordemServicoId, MultipartFile file) {
        OrdemServico ordemServico = ordemServicoRepository.findById(ordemServicoId)
                 .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + ordemServicoId));

        // Verifica se já existe uma assinatura para esta OS
        Optional<AssinaturaOS> existingAssinatura = assinaturaOSRepository.findByOrdemServicoId(ordemServicoId);

        // Se já existe, removemos o arquivo antigo antes de salvar o novo
        if (existingAssinatura.isPresent()) {
            deleteAssinaturaFile(existingAssinatura.get().getCaminhoArquivo());
            assinaturaOSRepository.delete(existingAssinatura.get()); // Remove o registro antigo
        }

        // TODO: Validar o tipo e tamanho do arquivo (permitir apenas imagens, limitar tamanho)
        // TODO: Implementar validação de segurança (apenas técnico da OS ou admin pode fazer upload/substituir)

        try {
            Path uploadPath = Paths.get(uploadDirAssinaturas);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Gera um nome de arquivo único
            // Mantemos a extensão original ou padronizamos para .png/svg
            String extensao = Optional.ofNullable(file.getOriginalFilename())
                                     .filter(f -> f.contains("."))
                                     .map(f -> "." + f.substring(f.lastIndexOf(".") + 1)).orElse(".bin");
            String nomeArquivoUnico = UUID.randomUUID().toString() + extensao;
            Path filePath = uploadPath.resolve(nomeArquivoUnico);
            Files.copy(file.getInputStream(), filePath); // Salva o arquivo

            // Cria o novo registro no banco de dados
            AssinaturaOS assinaturaOS = new AssinaturaOS();
            assinaturaOS.setOrdemServico(ordemServico);
            assinaturaOS.setCaminhoArquivo(filePath.toString()); // Salva o caminho
            assinaturaOS.setTipoConteudo(file.getContentType());
            assinaturaOS.setTamanhoArquivo(file.getSize());
            assinaturaOS.setDataHoraColeta(LocalDateTime.now());

            AssinaturaOS savedAssinaturaOS = assinaturaOSRepository.save(assinaturaOS);
            return convertToDTO(savedAssinaturaOS);

        } catch (IOException e) {
            throw new BusinessException("Falha ao salvar o arquivo da assinatura: " + e.getMessage());
        }
    }

    // Método para deletar a assinatura (arquivo e registro no banco)
    public void deleteAssinatura(Long ordemServicoId) {
        AssinaturaOS assinatura = assinaturaOSRepository.findByOrdemServicoId(ordemServicoId)
             .orElseThrow(() -> new ResourceNotFoundException("Assinatura para a OS " + ordemServicoId + " não encontrada."));

        // TODO: Validar permissão para deletar a assinatura

        deleteAssinaturaFile(assinatura.getCaminhoArquivo()); // Deleta o arquivo físico
        assinaturaOSRepository.delete(assinatura); // Deleta o registro do banco
    }

     // Método auxiliar para deletar o arquivo físico
    private void deleteAssinaturaFile(String filePathString) {
         if (filePathString != null) {
             try {
                Path filePath = Paths.get(filePathString);
                Files.deleteIfExists(filePath);
             } catch (IOException e) {
                 // Apenas loga o erro, não impede a exclusão do registro do banco
                 System.err.println("Erro ao deletar arquivo de assinatura: " + e.getMessage());
             }
         }
    }

     // Método para servir o arquivo (download)
     public Path loadAssinaturaAsResource(Long id) {
         AssinaturaOS assinatura = assinaturaOSRepository.findById(id)
             .orElseThrow(() -> new ResourceNotFoundException("Assinatura não encontrada com ID: " + id));

          // TODO: Validar permissão para acessar a assinatura

         Path filePath = Paths.get(assinatura.getCaminhoArquivo());
         if (Files.exists(filePath) && Files.isReadable(filePath)) {
             return filePath;
         } else {
             throw new ResourceNotFoundException("Arquivo da assinatura não encontrado ou inacessível.");
         }
     }


    private AssinaturaOSResponseDTO convertToDTO(AssinaturaOS assinaturaOS) {
        AssinaturaOSResponseDTO dto = new AssinaturaOSResponseDTO();
        dto.setId(assinaturaOS.getId());
        dto.setOrdemServicoId(assinaturaOS.getOrdemServico().getId());
        // Gera a URL de acesso para o endpoint de download
        // TODO: Substituir localhost:8080 pela URL base da sua API em produção
        dto.setUrlAcesso("/api/ordens-servico/" + assinaturaOS.getOrdemServico().getId() + "/assinatura/" + assinaturaOS.getId() + "/download");
        dto.setTipoConteudo(assinaturaOS.getTipoConteudo());
        dto.setTamanhoArquivo(assinaturaOS.getTamanhoArquivo());
        dto.setDataHoraColeta(assinaturaOS.getDataHoraColeta());
        return dto;
    }
}