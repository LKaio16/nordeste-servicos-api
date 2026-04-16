package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.AssinaturaOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssinaturaOSRepository extends JpaRepository<AssinaturaOS, Long> {

    Optional<AssinaturaOS> findByOrdemServicoId(Long ordemServicoId);
}