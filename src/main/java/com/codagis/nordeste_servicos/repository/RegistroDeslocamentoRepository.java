package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.RegistroDeslocamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroDeslocamentoRepository extends JpaRepository<RegistroDeslocamento, Long> {
    List<RegistroDeslocamento> findByOrdemServicoId(Long ordemServicoId);
    List<RegistroDeslocamento> findByTecnicoId(Long tecnicoId);
}