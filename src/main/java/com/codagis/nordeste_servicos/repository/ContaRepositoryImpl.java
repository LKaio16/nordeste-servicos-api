package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.dto.ContaListItemDTO;
import com.codagis.nordeste_servicos.model.Cliente;
import com.codagis.nordeste_servicos.model.Conta;
import com.codagis.nordeste_servicos.model.Fornecedor;
import com.codagis.nordeste_servicos.model.StatusConta;
import com.codagis.nordeste_servicos.model.TipoConta;
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

public class ContaRepositoryImpl implements ContaRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ContaListItemDTO> findListItemsByFilters(Long clienteId, Long fornecedorId, TipoConta tipo, StatusConta status, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ContaListItemDTO> query = cb.createQuery(ContaListItemDTO.class);
        Root<Conta> conta = query.from(Conta.class);
        Join<Conta, Cliente> clienteJoin = conta.join("cliente", JoinType.LEFT);
        Join<Conta, Fornecedor> fornecedorJoin = conta.join("fornecedor", JoinType.LEFT);

        List<Predicate> predicates = buildPredicates(cb, conta, clienteJoin, fornecedorJoin, clienteId, fornecedorId, tipo, status);

        query.select(cb.construct(
                ContaListItemDTO.class,
                conta.get("id"),
                conta.get("tipo"),
                conta.get("descricao"),
                clienteJoin.get("nomeCompleto"),
                fornecedorJoin.get("nome"),
                conta.get("valor"),
                conta.get("dataVencimento"),
                conta.get("status")
        ));
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.desc(conta.get("id")));

        return entityManager.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public long countByFilters(Long clienteId, Long fornecedorId, TipoConta tipo, StatusConta status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Conta> conta = query.from(Conta.class);
        Join<Conta, Cliente> clienteJoin = conta.join("cliente", JoinType.LEFT);
        Join<Conta, Fornecedor> fornecedorJoin = conta.join("fornecedor", JoinType.LEFT);

        List<Predicate> predicates = buildPredicates(cb, conta, clienteJoin, fornecedorJoin, clienteId, fornecedorId, tipo, status);
        query.select(cb.countDistinct(conta));
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getSingleResult();
    }

    private List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            Root<Conta> conta,
            Join<Conta, Cliente> clienteJoin,
            Join<Conta, Fornecedor> fornecedorJoin,
            Long clienteId,
            Long fornecedorId,
            TipoConta tipo,
            StatusConta status
    ) {
        List<Predicate> predicates = new ArrayList<>();
        if (clienteId != null) {
            predicates.add(cb.equal(clienteJoin.get("id"), clienteId));
        }
        if (fornecedorId != null) {
            predicates.add(cb.equal(fornecedorJoin.get("id"), fornecedorId));
        }
        if (tipo != null) {
            predicates.add(cb.equal(conta.get("tipo"), tipo));
        }
        if (status != null) {
            predicates.add(cb.equal(conta.get("status"), status));
        }
        return predicates;
    }
}
