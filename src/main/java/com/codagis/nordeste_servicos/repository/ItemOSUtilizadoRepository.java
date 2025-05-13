package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.ItemOSUtilizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemOSUtilizadoRepository extends JpaRepository<ItemOSUtilizado, Long> {
    List<ItemOSUtilizado> findByOrdemServicoId(Long ordemServicoId);

    // Opcional: Método para encontrar um item específico de uma OS por peça
    // Optional<ItemOSUtilizado> findByOrdemServicoIdAndPecaMaterialId(Long ordemServicoId, Long pecaMaterialId);
}