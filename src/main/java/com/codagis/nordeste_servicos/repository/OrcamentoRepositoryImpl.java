package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.dto.OrcamentoListItemDTO;
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
        Join<Orcamento, Cliente> clienteJoin = orcamento.join("cliente", JoinType.INNER);

        List<Predicate> predicates = buildPredicates(cb, orcamento, clienteJoin, clienteId, status, ordemServicoOrigemId, searchTerm);

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.desc(orcamento.get("id")));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<OrcamentoListItemDTO> findListItemsByFilters(Long clienteId, StatusOrcamento status, Long ordemServicoOrigemId, String searchTerm, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrcamentoListItemDTO> query = cb.createQuery(OrcamentoListItemDTO.class);
        Root<Orcamento> orcamento = query.from(Orcamento.class);
        Join<Orcamento, Cliente> clienteJoin = orcamento.join("cliente", JoinType.INNER);

        List<Predicate> predicates = buildPredicates(cb, orcamento, clienteJoin, clienteId, status, ordemServicoOrigemId, searchTerm);

        query.select(cb.construct(
                OrcamentoListItemDTO.class,
                orcamento.get("id"),
                orcamento.get("numeroOrcamento"),
                orcamento.get("status"),
                clienteJoin.get("nomeCompleto"),
                orcamento.get("dataValidade"),
                orcamento.get("valorTotal")
        ));
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.desc(orcamento.get("id")));

        return entityManager.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public long countByFilters(Long clienteId, StatusOrcamento status, Long ordemServicoOrigemId, String searchTerm) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Orcamento> orcamento = query.from(Orcamento.class);
        Join<Orcamento, Cliente> clienteJoin = orcamento.join("cliente", JoinType.INNER);
        List<Predicate> predicates = buildPredicates(cb, orcamento, clienteJoin, clienteId, status, ordemServicoOrigemId, searchTerm);

        query.select(cb.countDistinct(orcamento));
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getSingleResult();
    }

    private List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            Root<Orcamento> orcamento,
            Join<Orcamento, Cliente> clienteJoin,
            Long clienteId,
            StatusOrcamento status,
            Long ordemServicoOrigemId,
            String searchTerm
    ) {
        List<Predicate> predicates = new ArrayList<>();
        if (clienteId != null) predicates.add(cb.equal(clienteJoin.get("id"), clienteId));
        if (status != null) predicates.add(cb.equal(orcamento.get("status"), status));
        if (ordemServicoOrigemId != null) predicates.add(cb.equal(orcamento.get("ordemServicoOrigem").get("id"), ordemServicoOrigemId));
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(orcamento.get("numeroOrcamento")), likePattern),
                    cb.like(cb.lower(clienteJoin.get("nomeCompleto")), likePattern)
            ));
        }
        return predicates;
    }
} 