package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class OrcamentoRepositoryImpl implements OrcamentoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Orcamento> findByFilters(Long clienteId, StatusOrcamento status, Long ordemServicoOrigemId, String searchTerm) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Orcamento> query = cb.createQuery(Orcamento.class);
        Root<Orcamento> orcamento = query.from(Orcamento.class);
        orcamento.fetch("cliente", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();

        if (clienteId != null) {
            predicates.add(cb.equal(orcamento.get("cliente").get("id"), clienteId));
        }
        if (status != null) {
            predicates.add(cb.equal(orcamento.get("status"), status));
        }
        if (ordemServicoOrigemId != null) {
            predicates.add(cb.equal(orcamento.get("ordemServicoOrigem").get("id"), ordemServicoOrigemId));
        }

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            Predicate searchPredicate = cb.or(
                    cb.like(cb.lower(orcamento.get("numeroOrcamento")), likePattern),
                    cb.like(cb.lower(orcamento.get("cliente").get("nomeCompleto")), likePattern)
            );
            predicates.add(searchPredicate);
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.desc(orcamento.get("id")));

        return entityManager.createQuery(query).getResultList();
    }
} 