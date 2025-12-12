package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.Recibo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReciboRepository extends JpaRepository<Recibo, Long> {
    List<Recibo> findAllByOrderByDataCriacaoDesc();
}

