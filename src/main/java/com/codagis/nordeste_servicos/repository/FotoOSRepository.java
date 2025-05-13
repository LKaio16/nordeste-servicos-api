package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.FotoOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoOSRepository extends JpaRepository<FotoOS, Long> {
    List<FotoOS> findByOrdemServicoId(Long ordemServicoId);
}