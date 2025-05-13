package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.TipoServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoServicoRepository extends JpaRepository<TipoServico, Long> {
    Optional<TipoServico> findByDescricao(String descricao);
}