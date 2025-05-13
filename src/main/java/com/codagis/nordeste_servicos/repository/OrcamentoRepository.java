package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.Orcamento;
import com.codagis.nordeste_servicos.model.StatusOrcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    Optional<Orcamento> findByNumeroOrcamento(String numeroOrcamento);
    List<Orcamento> findByClienteId(Long clienteId);
    List<Orcamento> findByStatus(StatusOrcamento status);
    Optional<Orcamento> findByOrdemServicoOrigemId(Long ordemServicoId);
}