package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.dto.OrdemServicoListItemDTO;
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
        return findByFilters(tecnicoId, clienteId, status, searchTerm, 0, Integer.MAX_VALUE);
    }

    @Override
    public List<OrdemServico> findByFilters(Long tecnicoId, Long clienteId, StatusOS status, String searchTerm, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrdemServico> query = cb.createQuery(OrdemServico.class);
        Root<OrdemServico> os = query.from(OrdemServico.class);
        os.fetch("cliente", JoinType.INNER);
        os.fetch("tecnicoAtribuido", JoinType.LEFT);
        Join<OrdemServico, Cliente> clienteJoin = os.join("cliente", JoinType.INNER);
        Join<OrdemServico, Usuario> tecnicoJoin = os.join("tecnicoAtribuido", JoinType.LEFT);
        // Não faz fetch de "fotos" - evita carregar foto_base64 (LOB pesado da tabela fotoos)

        List<Predicate> predicates = buildPredicates(cb, os, clienteJoin, tecnicoJoin, tecnicoId, clienteId, status, searchTerm);
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        if (tecnicoId != null && status == null) {
            query.orderBy(
                    cb.asc(buildStatusPriorityOrder(cb, os)),
                    cb.desc(os.get("dataAgendamento")),
                    cb.desc(os.get("dataAbertura")),
                    cb.desc(os.get("id"))
            );
        } else {
            query.orderBy(cb.desc(os.get("id")));
        }

        // Aplica paginação
        return entityManager.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public List<OrdemServicoListItemDTO> findListItemsByFilters(Long tecnicoId, Long clienteId, StatusOS status, String searchTerm, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrdemServicoListItemDTO> query = cb.createQuery(OrdemServicoListItemDTO.class);
        Root<OrdemServico> os = query.from(OrdemServico.class);
        Join<OrdemServico, Cliente> clienteJoin = os.join("cliente", JoinType.INNER);
        Join<OrdemServico, Usuario> tecnicoJoin = os.join("tecnicoAtribuido", JoinType.LEFT);

        List<Predicate> predicates = buildPredicates(cb, os, clienteJoin, tecnicoJoin, tecnicoId, clienteId, status, searchTerm);

        query.select(cb.construct(
                OrdemServicoListItemDTO.class,
                os.get("id"),
                os.get("numeroOS"),
                os.get("status"),
                os.get("dataAbertura"),
                clienteJoin.get("nomeCompleto"),
                tecnicoJoin.get("nome")
        ));
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        if (tecnicoId != null && status == null) {
            query.orderBy(
                    cb.asc(buildStatusPriorityOrder(cb, os)),
                    cb.desc(os.get("dataAgendamento")),
                    cb.desc(os.get("dataAbertura")),
                    cb.desc(os.get("id"))
            );
        } else {
            query.orderBy(cb.desc(os.get("id")));
        }

        return entityManager.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public long countByFilters(Long tecnicoId, Long clienteId, StatusOS status, String searchTerm) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<OrdemServico> os = query.from(OrdemServico.class);
        Join<OrdemServico, Cliente> clienteJoin = os.join("cliente", JoinType.INNER);
        Join<OrdemServico, Usuario> tecnicoJoin = os.join("tecnicoAtribuido", JoinType.LEFT);

        List<Predicate> predicates = buildPredicates(cb, os, clienteJoin, tecnicoJoin, tecnicoId, clienteId, status, searchTerm);
        query.select(cb.countDistinct(os));
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getSingleResult();
    }

    private List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            Root<OrdemServico> os,
            Join<OrdemServico, Cliente> clienteJoin,
            Join<OrdemServico, Usuario> tecnicoJoin,
            Long tecnicoId,
            Long clienteId,
            StatusOS status,
            String searchTerm
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (tecnicoId != null) {
            predicates.add(cb.equal(tecnicoJoin.get("id"), tecnicoId));
        }
        if (clienteId != null) {
            predicates.add(cb.equal(clienteJoin.get("id"), clienteId));
        }
        if (status != null) {
            predicates.add(cb.equal(os.get("status"), status));
        }
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(os.get("id").as(String.class)), likePattern),
                    cb.like(cb.lower(os.get("numeroOS")), likePattern),
                    cb.like(cb.lower(clienteJoin.get("nomeCompleto")), likePattern),
                    cb.like(cb.lower(tecnicoJoin.get("nome")), likePattern)
            ));
        }

        return predicates;
    }

    private Expression<Integer> buildStatusPriorityOrder(CriteriaBuilder cb, Root<OrdemServico> os) {
        Path<StatusOS> statusPath = os.get("status");
        return cb.<Integer>selectCase()
                .when(cb.equal(statusPath, StatusOS.EM_ANDAMENTO), 0)
                .when(cb.equal(statusPath, StatusOS.EM_ABERTO), 1)
                .when(cb.equal(statusPath, StatusOS.AGUARDANDO_APROVACAO), 2)
                .when(cb.equal(statusPath, StatusOS.ATRIBUIDA), 3)
                .when(cb.equal(statusPath, StatusOS.PENDENTE_PECAS), 4)
                .when(cb.equal(statusPath, StatusOS.CANCELADA), 5)
                .when(cb.equal(statusPath, StatusOS.CONCLUIDA), 6)
                .when(cb.equal(statusPath, StatusOS.ENCERRADA), 7)
                .otherwise(9);
    }
} 