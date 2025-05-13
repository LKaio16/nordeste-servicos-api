package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.PecaMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PecaMaterialRepository extends JpaRepository<PecaMaterial, Long> {
    Optional<PecaMaterial> findByCodigo(String codigo);
}