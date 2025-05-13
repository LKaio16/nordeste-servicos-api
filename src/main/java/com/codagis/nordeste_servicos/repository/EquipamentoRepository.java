package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
    // Método para encontrar equipamentos por cliente
    List<Equipamento> findByClienteId(Long clienteId);
}