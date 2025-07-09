package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.RegistroTempo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroTempoRepository extends JpaRepository<RegistroTempo, Long> {
    List<RegistroTempo> findByOrdemServicoId(Long ordemServicoId);

    Optional<RegistroTempo> findByOrdemServicoIdAndTecnicoIdAndHoraTerminoIsNull(Long ordemServicoId, Long tecnicoId);
}