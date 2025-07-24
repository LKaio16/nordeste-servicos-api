package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long>, OrcamentoRepositoryCustom {
    Optional<Orcamento> findByNumeroOrcamento(String numeroOrcamento);
}