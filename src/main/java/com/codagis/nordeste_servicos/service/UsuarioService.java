package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.DesempenhoTecnicoDTO;
import com.codagis.nordeste_servicos.dto.UsuarioRequestDTO;
import com.codagis.nordeste_servicos.dto.UsuarioResponseDTO;
import com.codagis.nordeste_servicos.exception.BusinessException;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.model.PerfilUsuario;
import com.codagis.nordeste_servicos.model.StatusOS; // Importar StatusOS
import com.codagis.nordeste_servicos.model.Usuario;
import com.codagis.nordeste_servicos.repository.OrdemServicoRepository;
import com.codagis.nordeste_servicos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        if (usuarioRepository.findByEmail(usuarioRequestDTO.getEmail()).isPresent()) {
            throw new BusinessException("O e-mail informado já está em uso.");
        }
        if (usuarioRepository.findByCracha(usuarioRequestDTO.getCracha()).isPresent()) {
            throw new BusinessException("O crachá informado já está em uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(usuarioRequestDTO.getNome());
        usuario.setCracha(usuarioRequestDTO.getCracha());
        usuario.setEmail(usuarioRequestDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
        usuario.setPerfil(usuarioRequestDTO.getPerfil());
        usuario.setFotoPerfil(usuarioRequestDTO.getFotoPerfil());

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return convertToDTO(savedUsuario);
    }


    public UsuarioResponseDTO updateUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        Usuario existingUsuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        usuarioRepository.findByEmail(usuarioRequestDTO.getEmail()).ifPresent(user -> {
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
        existingUsuario.setEmail(usuarioRequestDTO.getEmail());
        existingUsuario.setPerfil(usuarioRequestDTO.getPerfil());
        existingUsuario.setFotoPerfil(usuarioRequestDTO.getFotoPerfil());

        Usuario updatedUsuario = usuarioRepository.save(existingUsuario);
        return convertToDTO(updatedUsuario);
    }

    @Transactional
    public void deleteUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }

        List<OrdemServico> ordensServico = ordemServicoRepository.findByTecnicoAtribuidoId(id);
        if (!ordensServico.isEmpty()) {
            throw new BusinessException("Não é possível excluir o usuário, pois ele está associado a ordens de serviço.");
        }

        usuarioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<DesempenhoTecnicoDTO> getDesempenhoTecnicos() {
        List<Usuario> tecnicos = usuarioRepository.findByPerfil(PerfilUsuario.TECNICO);

        return tecnicos.stream()
                .map(this::convertToDesempenhoDTO)
                .collect(Collectors.toList());
    }

    private DesempenhoTecnicoDTO convertToDesempenhoDTO(Usuario tecnico) {
        // LÓGICA ALTERADA: Conta o total de OS atribuídas ao técnico.
        int totalOS = ordemServicoRepository.countByTecnicoAtribuidoId(tecnico.getId());

        // Conta apenas as Ordens de Serviço com status CONCLUIDA.
        int osConcluidas = ordemServicoRepository.countByTecnicoAtribuidoIdAndStatus(tecnico.getId(), StatusOS.CONCLUIDA);

        // A métrica de desempenho agora é a razão entre OS concluídas e o total de OS atribuídas.
        // Se o técnico não tiver nenhuma OS, seu desempenho é 0.
        double desempenho = (totalOS > 0) ? ((double) osConcluidas / totalOS) : 0.0;

        return new DesempenhoTecnicoDTO(
                tecnico.getId(),
                tecnico.getNome(),
                tecnico.getFotoPerfil(),
                totalOS, // O total agora reflete todas as OS atribuídas
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
