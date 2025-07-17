package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.Equipamento;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, Long>, JpaSpecificationExecutor<Equipamento> {
    // Método para encontrar equipamentos por cliente
    List<Equipamento> findByClienteId(Long clienteId);

    default List<Equipamento> findAllWithFilters(Long clienteId, String searchTerm) {
        Specification<Equipamento> spec = Specification.where(null);

        if (clienteId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("cliente").get("id"), clienteId));
        }

        if (StringUtils.hasText(searchTerm)) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("tipo")), "%" + searchTerm.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("marcaModelo")), "%" + searchTerm.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("numeroSerieChassi")), "%" + searchTerm.toLowerCase() + "%")
                    )
            );
        }

        return findAll(spec);
    }
}