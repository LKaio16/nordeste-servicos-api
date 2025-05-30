package com.codagis.nordeste_servicos.service;

import com.codagis.nordeste_servicos.dto.UsuarioRequestDTO;
import com.codagis.nordeste_servicos.dto.UsuarioResponseDTO;
import com.codagis.nordeste_servicos.exception.ResourceNotFoundException;
import com.codagis.nordeste_servicos.model.Usuario;
import com.codagis.nordeste_servicos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

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
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioRequestDTO.getNome());
        usuario.setCracha(usuarioRequestDTO.getCracha());
        usuario.setEmail(usuarioRequestDTO.getEmail());
        // Criptografe a senha antes de definir no objeto usuário
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
        usuario.setPerfil(usuarioRequestDTO.getPerfil());

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return convertToDTO(savedUsuario);
    }


    public UsuarioResponseDTO updateUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        Usuario existingUsuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        existingUsuario.setNome(usuarioRequestDTO.getNome());
        existingUsuario.setCracha(usuarioRequestDTO.getCracha());
        existingUsuario.setEmail(usuarioRequestDTO.getEmail());
        // TODO: Implementar lógica para atualização de senha (pode exigir senha antiga ou ser um endpoint separado)
        existingUsuario.setPerfil(usuarioRequestDTO.getPerfil());

        Usuario updatedUsuario = usuarioRepository.save(existingUsuario);
        return convertToDTO(updatedUsuario);
    }

    public void deleteUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
             throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    // Método para encontrar usuário por email (útil para login)
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    private UsuarioResponseDTO convertToDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setCracha(usuario.getCracha());
        dto.setEmail(usuario.getEmail());
        dto.setPerfil(usuario.getPerfil());
        return dto;
    }

    private Usuario convertToEntity(UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioRequestDTO.getNome());
        usuario.setCracha(usuarioRequestDTO.getCracha());
        usuario.setEmail(usuarioRequestDTO.getEmail());
        usuario.setSenha(usuarioRequestDTO.getSenha());
        usuario.setPerfil(usuarioRequestDTO.getPerfil());
        return usuario;
    }
}