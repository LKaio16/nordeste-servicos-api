package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.DesempenhoTecnicoDTO;
import com.codagis.nordeste_servicos.dto.UsuarioRequestDTO;
import com.codagis.nordeste_servicos.dto.UsuarioResponseDTO;
import com.codagis.nordeste_servicos.exception.BusinessException;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.model.PerfilUsuario;
import com.codagis.nordeste_servicos.model.StatusOS;
import com.codagis.nordeste_servicos.model.Usuario;
import com.codagis.nordeste_servicos.repository.OrdemServicoRepository;
import com.codagis.nordeste_servicos.repository.UsuarioRepository;
import com.codagis.nordeste_servicos.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private GoogleCloudStorageService googleCloudStorageService;

    @Transactional
    public UsuarioResponseDTO uploadFotoPerfil(Long id, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Arquivo de imagem é obrigatório.");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        if (googleCloudStorageService == null) {
            throw new BusinessException("Upload de imagens não está configurado (Google Cloud Storage).");
        }
        byte[] bytes = file.getBytes();
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        String fotoUrl = googleCloudStorageService.uploadImageForUsuario(id, bytes, contentType, originalFilename);
        usuario.setFotoUrl(fotoUrl);
        usuarioRepository.save(usuario);
        return convertToDTO(usuario);
    }

    public List<UsuarioResponseDTO> findAllUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO findUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        return convertToDTO(usuario);
    }

    public UsuarioResponseDTO createUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        final String email = ValidationUtils.normalizeEmail(usuarioRequestDTO.getEmail());
        if (!ValidationUtils.isValidEmail(email)) {
            throw new BusinessException("Formato de e-mail inválido.");
        }
        usuarioRequestDTO.setEmail(email);

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new BusinessException("O e-mail informado já está em uso.");
        }
        if (usuarioRepository.findByCracha(usuarioRequestDTO.getCracha()).isPresent()) {
            throw new BusinessException("O crachá informado já está em uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(usuarioRequestDTO.getNome());
        usuario.setCracha(usuarioRequestDTO.getCracha());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
        usuario.setPerfil(usuarioRequestDTO.getPerfil());
        usuario.setFotoPerfil(usuarioRequestDTO.getFotoPerfil());

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return convertToDTO(savedUsuario);
    }

    public UsuarioResponseDTO updateUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        Usuario existingUsuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        final String email = ValidationUtils.normalizeEmail(usuarioRequestDTO.getEmail());
        if (!ValidationUtils.isValidEmail(email)) {
            throw new BusinessException("Formato de e-mail inválido.");
        }
        usuarioRequestDTO.setEmail(email);

        usuarioRepository.findByEmail(email).ifPresent(user -> {
            if (!user.getId().equals(id)) {
                throw new BusinessException("O e-mail informado já está em uso por outro usuário.");
            }
        });

        usuarioRepository.findByCracha(usuarioRequestDTO.getCracha()).ifPresent(user -> {
            if (!user.getId().equals(id)) {
                throw new BusinessException("O crachá informado já está em uso por outro usuário.");
            }
        });

        existingUsuario.setNome(usuarioRequestDTO.getNome());
        existingUsuario.setCracha(usuarioRequestDTO.getCracha());
        existingUsuario.setEmail(email);
        existingUsuario.setPerfil(usuarioRequestDTO.getPerfil());
        existingUsuario.setFotoPerfil(usuarioRequestDTO.getFotoPerfil());

        if (usuarioRequestDTO.getSenha() != null && !usuarioRequestDTO.getSenha().trim().isEmpty()) {
            if (usuarioRequestDTO.getSenha().length() < 6) {
                throw new BusinessException("A senha deve ter no mínimo 6 caracteres.");
            }
            existingUsuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
        }

        Usuario updatedUsuario = usuarioRepository.save(existingUsuario);
        return convertToDTO(updatedUsuario);
    }

    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty() || newPassword.length() < 6) {
            throw new BusinessException("A nova senha deve ter no mínimo 6 caracteres.");
        }
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));

        usuario.setSenha(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void deleteUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }

        List<OrdemServico> ordensServico = ordemServicoRepository.findByFilters(id, null, null, null);
        if (!ordensServico.isEmpty()) {
            throw new BusinessException("Não é possível excluir o usuário, pois ele está associado a ordens de serviço.");
        }

        usuarioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Verifica senha com BCrypt. Se o banco ainda tiver senha em texto plano (legado), confere e regrava com hash.
     */
    @Transactional
    public boolean matchesPassword(Usuario usuario, String senhaInformada) {
        if (senhaInformada == null || usuario.getSenha() == null) {
            return false;
        }
        String stored = usuario.getSenha();
        if (passwordEncoder.matches(senhaInformada, stored)) {
            return true;
        }
        boolean looksBcrypt = stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$");
        if (!looksBcrypt && stored.equals(senhaInformada)) {
            usuario.setSenha(passwordEncoder.encode(senhaInformada));
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByIdEntity(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<DesempenhoTecnicoDTO> getDesempenhoTecnicos() {
        List<Usuario> tecnicos = usuarioRepository.findByPerfil(PerfilUsuario.TECNICO);
        if (tecnicos.isEmpty()) {
            return List.of();
        }

        List<Long> tecnicoIds = tecnicos.stream().map(Usuario::getId).collect(Collectors.toList());
        List<Object[]> rows = ordemServicoRepository.getTecnicoPerformanceCounts(tecnicoIds, StatusOS.CONCLUIDA);
        Map<Long, int[]> metricsByTecnico = new HashMap<>();
        for (Object[] row : rows) {
            Long tecnicoId = (Long) row[0];
            int total = row[1] != null ? ((Long) row[1]).intValue() : 0;
            int concluidas = row[2] != null ? ((Long) row[2]).intValue() : 0;
            metricsByTecnico.put(tecnicoId, new int[]{total, concluidas});
        }

        return tecnicos.stream()
                .map(tecnico -> convertToDesempenhoDTO(tecnico, metricsByTecnico))
                .collect(Collectors.toList());
    }

    private DesempenhoTecnicoDTO convertToDesempenhoDTO(Usuario tecnico, Map<Long, int[]> metricsByTecnico) {
        int[] metrics = metricsByTecnico.getOrDefault(tecnico.getId(), new int[]{0, 0});
        int totalOS = metrics[0];
        int osConcluidas = metrics[1];
        double desempenho = (totalOS > 0) ? ((double) osConcluidas / totalOS) : 0.0;

        return new DesempenhoTecnicoDTO(
                tecnico.getId(),
                tecnico.getNome(),
                tecnico.getFotoPerfil(),
                tecnico.getFotoUrl(),
                totalOS,
                desempenho
        );
    }

    private UsuarioResponseDTO convertToDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setCracha(usuario.getCracha());
        dto.setEmail(usuario.getEmail());
        dto.setPerfil(usuario.getPerfil());
        dto.setFotoPerfil(usuario.getFotoPerfil());
        dto.setFotoUrl(usuario.getFotoUrl());
        return dto;
    }

    private Usuario convertToEntity(UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioRequestDTO.getNome());
        usuario.setCracha(usuarioRequestDTO.getCracha());
        usuario.setEmail(usuarioRequestDTO.getEmail());
        usuario.setSenha(usuarioRequestDTO.getSenha());
        usuario.setPerfil(usuarioRequestDTO.getPerfil());
        usuario.setFotoPerfil(usuarioRequestDTO.getFotoPerfil());
        return usuario;
    }
}
