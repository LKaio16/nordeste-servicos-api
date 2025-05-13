package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.model.StatusOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    Optional<OrdemServico> findByNumeroOS(String numeroOS);

    List<OrdemServico> findByTecnicoAtribuidoId(Long tecnicoId);

    List<OrdemServico> findByClienteId(Long clienteId);

    List<OrdemServico> findByStatus(StatusOS status);

    // Métodos para busca com filtros combinados podem ser adicionados aqui
    // Ex: List<OrdemServico> findByTecnicoAtribuidoIdAndStatus(Long tecnicoId, StatusOS status);
}