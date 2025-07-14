package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.PerfilUsuario;
import com.codagis.nordeste_servicos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByPerfil(PerfilUsuario perfil);
}