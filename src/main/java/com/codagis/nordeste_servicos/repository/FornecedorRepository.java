package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.Fornecedor;
import com.codagis.nordeste_servicos.model.StatusFornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    Optional<Fornecedor> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);

    List<Fornecedor> findByStatus(StatusFornecedor status);

    List<Fornecedor> findByNomeContainingIgnoreCase(String nome);
}
