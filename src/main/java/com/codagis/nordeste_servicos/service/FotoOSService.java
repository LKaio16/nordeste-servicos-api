package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.FotoOSResponseDTO;
import com.codagis.nordeste_servicos.exception.BusinessException;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.FotoOS;
import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.repository.FotoOSRepository;
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
import java.util.List;
import java.util.UUID; // Para gerar nomes de arquivo únicos
import java.util.stream.Collectors;

@Service
public class FotoOSService {

    @Autowired
    private FotoOSRepository fotoOSRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Value("${app.upload.dir:${user.home}/uploads}") // Configuração para o diretório de upload
    private String uploadDir;

    // Método para listar fotos de uma OS
    public List<FotoOSResponseDTO> findFotosByOrdemServicoId(Long ordemServicoId) {
        List<FotoOS> fotos = fotoOSRepository.findByOrdemServicoId(ordemServicoId);
        return fotos.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
    }

     public FotoOSResponseDTO findFotoById(Long id) {
         FotoOS foto = fotoOSRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Foto não encontrada com ID: " + id));
         return convertToDTO(foto);
     }

    // Método para salvar a foto (arquivo) e criar o registro no banco
    public FotoOSResponseDTO saveFoto(Long ordemServicoId, MultipartFile file) {
        OrdemServico ordemServico = ordemServicoRepository.findById(ordemServicoId)
                 .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço não encontrada com ID: " + ordemServicoId));

        // TODO: Validar o tipo e tamanho do arquivo (permitir apenas imagens, limitar tamanho)
        // TODO: Implementar validação de segurança (apenas técnico da OS ou admin pode fazer upload)

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Gera um nome de arquivo único
            String nomeArquivoUnico = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(nomeArquivoUnico);
            Files.copy(file.getInputStream(), filePath); // Salva o arquivo no diretório

            // Cria o registro no banco de dados
            FotoOS fotoOS = new FotoOS();
            fotoOS.setOrdemServico(ordemServico);
            fotoOS.setCaminhoArquivo(filePath.toString()); // Salva o caminho local
            fotoOS.setNomeArquivoOriginal(file.getOriginalFilename());
            fotoOS.setTipoConteudo(file.getContentType());
            fotoOS.setTamanhoArquivo(file.getSize());
            fotoOS.setDataUpload(LocalDateTime.now());

            FotoOS savedFotoOS = fotoOSRepository.save(fotoOS);
            return convertToDTO(savedFotoOS);

        } catch (IOException e) {
            // Tratar erro de I/O ao salvar o arquivo
            throw new BusinessException("Falha ao salvar o arquivo da foto: " + e.getMessage());
        }
    }

    // Método para deletar a foto (arquivo e registro no banco)
    public void deleteFoto(Long id) {
        FotoOS foto = fotoOSRepository.findById(id)
             .orElseThrow(() -> new ResourceNotFoundException("Foto não encontrada com ID: " + id));

        // TODO: Validar permissão para deletar a foto

        try {
            // Deleta o arquivo do sistema de arquivos
            Path filePath = Paths.get(foto.getCaminhoArquivo());
            Files.deleteIfExists(filePath);

            // Deleta o registro do banco de dados
            fotoOSRepository.delete(foto);

        } catch (IOException e) {
            // Tratar erro de I/O ao deletar o arquivo
            throw new BusinessException("Falha ao deletar o arquivo da foto: " + e.getMessage());
        }
    }

     // Método para servir o arquivo (download)
     public Path loadFotoAsResource(Long id) {
         FotoOS foto = fotoOSRepository.findById(id)
             .orElseThrow(() -> new ResourceNotFoundException("Foto não encontrada com ID: " + id));

          // TODO: Validar permissão para acessar a foto (ela pertence a uma OS que o usuário tem acesso?)

         Path filePath = Paths.get(foto.getCaminhoArquivo());
         if (Files.exists(filePath) && Files.isReadable(filePath)) {
             return filePath;
         } else {
             throw new ResourceNotFoundException("Arquivo da foto não encontrado ou inacessível.");
         }
     }


    private FotoOSResponseDTO convertToDTO(FotoOS fotoOS) {
        FotoOSResponseDTO dto = new FotoOSResponseDTO();
        dto.setId(fotoOS.getId());
        dto.setOrdemServicoId(fotoOS.getOrdemServico().getId());
        // Gera a URL de acesso para o endpoint de download
        // TODO: Substituir localhost:8080 pela URL base da sua API em produção
        dto.setUrlAcesso("/api/ordens-servico/" + fotoOS.getOrdemServico().getId() + "/fotos/" + fotoOS.getId() + "/download");
        dto.setNomeArquivoOriginal(fotoOS.getNomeArquivoOriginal());
        dto.setTipoConteudo(fotoOS.getTipoConteudo());
        dto.setTamanhoArquivo(fotoOS.getTamanhoArquivo());
        dto.setDataUpload(fotoOS.getDataUpload());
        // Opcional: setar descrição se houver
        return dto;
    }

    // Não precisamos de um convertToEntity completo aqui, pois a criação é feita diretamente no serviço
    // baseada no MultipartFile e no OrdemServico
}