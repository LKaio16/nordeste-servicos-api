package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.ItemOrcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemOrcamentoRepository extends JpaRepository<ItemOrcamento, Long> {
    List<ItemOrcamento> findByOrcamentoId(Long orcamentoId);

    // Método para somar os subtotais dos itens de um orçamento específico
    // @Query("SELECT SUM(i.subtotal) FROM ItemOrcamento i WHERE i.orcamento.id = :orcamentoId")
    // Double sumSubtotalByOrcamentoId(@Param("orcamentoId") Long orcamentoId);
    // Vamos fazer a soma no serviço por enquanto
}