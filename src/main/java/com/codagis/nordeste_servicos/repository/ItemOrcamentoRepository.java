package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.ItemOrcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemOrcamentoRepository extends JpaRepository<ItemOrcamento, Long> {
    List<ItemOrcamento> findByOrcamentoId(Long orcamentoId);

}