package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.model.StatusOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long>, OrdemServicoRepositoryCustom {
    Optional<OrdemServico> findByNumeroOS(String numeroOS);

    // Conta o total de OS atribuídas a um técnico
    int countByTecnicoAtribuidoId(Long tecnicoId);

    // NOVO MÉTODO: Conta o total de OS de um técnico com um status específico
    int countByTecnicoAtribuidoIdAndStatus(Long tecnicoId, StatusOS status);

    // Busca a última Ordem de Serviço criada, ordenada pelo ID decrescente
    OrdemServico findTopByOrderByIdDesc();

    // Métodos para estatísticas do dashboard (sem buscar todas as OS)
    long count();
    long countByStatus(StatusOS status);
    List<OrdemServico> findTop5ByOrderByDataAberturaDesc();

    @Query("""
            SELECT o.tecnicoAtribuido.id,
                   COUNT(o),
                   SUM(CASE WHEN o.status = :concluidaStatus THEN 1 ELSE 0 END)
            FROM OrdemServico o
            WHERE o.tecnicoAtribuido.id IN :tecnicoIds
            GROUP BY o.tecnicoAtribuido.id
            """)
    List<Object[]> getTecnicoPerformanceCounts(@Param("tecnicoIds") List<Long> tecnicoIds, @Param("concluidaStatus") StatusOS concluidaStatus);
}
