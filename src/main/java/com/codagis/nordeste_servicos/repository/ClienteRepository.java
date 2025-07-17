package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.Cliente;
import com.codagis.nordeste_servicos.model.TipoCliente;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {

    default List<Cliente> findAllWithFilters(String searchTerm, String tipoClienteStr) {
        Specification<Cliente> spec = Specification.where(null);

        if (StringUtils.hasText(searchTerm)) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("nomeCompleto")), "%" + searchTerm.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("cpfCnpj")), "%" + searchTerm.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("email")), "%" + searchTerm.toLowerCase() + "%")
                    )
            );
        }

        if (StringUtils.hasText(tipoClienteStr)) {
            try {
                TipoCliente tipoCliente = TipoCliente.valueOf(tipoClienteStr.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("tipoCliente"), tipoCliente));
            } catch (IllegalArgumentException e) {
                // Tipo de cliente inválido, pode logar um aviso se quiser
            }
        }

        return findAll(spec);
    }
}