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
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    Optional<OrdemServico> findByNumeroOS(String numeroOS);

    List<OrdemServico> findByTecnicoAtribuidoId(Long tecnicoId);

    List<OrdemServico> findByClienteId(Long clienteId);

    List<OrdemServico> findByStatus(StatusOS status);

    // Adicionando a busca por searchTerm
    @Query("SELECT os FROM OrdemServico os JOIN os.cliente c LEFT JOIN os.tecnicoAtribuido t " +
            "WHERE (:searchTerm IS NULL OR " +
            "CAST(os.id AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.nomeCompleto) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<OrdemServico> searchByTerm(@Param("searchTerm") String searchTerm);


    // Conta o total de OS atribuídas a um técnico
    int countByTecnicoAtribuidoId(Long tecnicoId);

    // NOVO MÉTODO: Conta o total de OS de um técnico com um status específico
    int countByTecnicoAtribuidoIdAndStatus(Long tecnicoId, StatusOS status);

    // Busca a última Ordem de Serviço criada, ordenada pelo ID decrescente
    OrdemServico findTopByOrderByIdDesc();

}
