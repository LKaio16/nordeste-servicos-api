package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class OrdemServicoRepositoryImpl implements OrdemServicoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<OrdemServico> findByFilters(Long tecnicoId, Long clienteId, StatusOS status, String searchTerm) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrdemServico> query = cb.createQuery(OrdemServico.class);
        Root<OrdemServico> os = query.from(OrdemServico.class);
        os.fetch("cliente", JoinType.INNER);
        os.fetch("tecnicoAtribuido", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (tecnicoId != null) {
            predicates.add(cb.equal(os.get("tecnicoAtribuido").get("id"), tecnicoId));
        }
        if (clienteId != null) {
            predicates.add(cb.equal(os.get("cliente").get("id"), clienteId));
        }
        if (status != null) {
            predicates.add(cb.equal(os.get("status"), status));
        }

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            Predicate searchPredicate = cb.or(
                    cb.like(cb.lower(os.get("id").as(String.class)), likePattern),
                    cb.like(cb.lower(os.get("cliente").get("nomeCompleto")), likePattern),
                    cb.like(cb.lower(os.get("tecnicoAtribuido").get("nome")), likePattern),
                    cb.like(cb.lower(os.get("problemaRelatado")), likePattern)
            );
            predicates.add(searchPredicate);
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.desc(os.get("id")));

        return entityManager.createQuery(query).getResultList();
    }
} 