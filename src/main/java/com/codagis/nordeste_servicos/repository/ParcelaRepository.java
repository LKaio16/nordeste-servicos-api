package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.Parcela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParcelaRepository extends JpaRepository<Parcela, Long> {

    List<Parcela> findByContaIdOrderByNumeroParcela(Long contaId);

    void deleteByConta_Id(Long contaId);
}
