package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.dto.NotaFiscalListItemDTO;
import com.codagis.nordeste_servicos.model.Cliente;
import com.codagis.nordeste_servicos.model.Fornecedor;
import com.codagis.nordeste_servicos.model.NotaFiscal;
import com.codagis.nordeste_servicos.model.TipoNotaFiscal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class NotaFiscalRepositoryImpl implements NotaFiscalRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<NotaFiscalListItemDTO> findListItemsByFilters(Long fornecedorId, Long clienteId, TipoNotaFiscal tipo, String searchTerm, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<NotaFiscalListItemDTO> query = cb.createQuery(NotaFiscalListItemDTO.class);
        Root<NotaFiscal> notaFiscal = query.from(NotaFiscal.class);
        Join<NotaFiscal, Fornecedor> fornecedorJoin = notaFiscal.join("fornecedor", JoinType.LEFT);
        Join<NotaFiscal, Cliente> clienteJoin = notaFiscal.join("cliente", JoinType.LEFT);

        List<Predicate> predicates = buildPredicates(cb, notaFiscal, fornecedorJoin, clienteJoin, fornecedorId, clienteId, tipo, searchTerm);

        query.select(cb.construct(
                NotaFiscalListItemDTO.class,
                notaFiscal.get("id"),
                notaFiscal.get("tipo"),
                notaFiscal.get("numeroNota"),
                notaFiscal.get("nomeEmitente"),
                fornecedorJoin.get("nome"),
                clienteJoin.get("nomeCompleto"),
                notaFiscal.get("dataEmissao"),
                notaFiscal.get("valorTotal")
        ));
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.desc(notaFiscal.get("id")));

        return entityManager.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public long countByFilters(Long fornecedorId, Long clienteId, TipoNotaFiscal tipo, String searchTerm) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<NotaFiscal> notaFiscal = query.from(NotaFiscal.class);
        Join<NotaFiscal, Fornecedor> fornecedorJoin = notaFiscal.join("fornecedor", JoinType.LEFT);
        Join<NotaFiscal, Cliente> clienteJoin = notaFiscal.join("cliente", JoinType.LEFT);

        List<Predicate> predicates = buildPredicates(cb, notaFiscal, fornecedorJoin, clienteJoin, fornecedorId, clienteId, tipo, searchTerm);
        query.select(cb.countDistinct(notaFiscal));
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getSingleResult();
    }

    private List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            Root<NotaFiscal> notaFiscal,
            Join<NotaFiscal, Fornecedor> fornecedorJoin,
            Join<NotaFiscal, Cliente> clienteJoin,
            Long fornecedorId,
            Long clienteId,
            TipoNotaFiscal tipo,
            String searchTerm
    ) {
        List<Predicate> predicates = new ArrayList<>();
        if (fornecedorId != null) {
            predicates.add(cb.equal(fornecedorJoin.get("id"), fornecedorId));
        }
        if (clienteId != null) {
            predicates.add(cb.equal(clienteJoin.get("id"), clienteId));
        }
        if (tipo != null) {
            predicates.add(cb.equal(notaFiscal.get("tipo"), tipo));
        }
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(notaFiscal.get("numeroNota")), likePattern),
                    cb.like(cb.lower(notaFiscal.get("nomeEmitente")), likePattern),
                    cb.like(cb.lower(fornecedorJoin.get("nome")), likePattern),
                    cb.like(cb.lower(clienteJoin.get("nomeCompleto")), likePattern)
            ));
        }
        return predicates;
    }
}
