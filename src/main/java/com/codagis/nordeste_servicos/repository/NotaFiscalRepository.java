package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.NotaFiscal;
import com.codagis.nordeste_servicos.model.TipoNotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {

    Optional<NotaFiscal> findByNumeroNota(String numeroNota);

    boolean existsByNumeroNota(String numeroNota);

    List<NotaFiscal> findByTipo(TipoNotaFiscal tipo);

    List<NotaFiscal> findByFornecedorId(Long fornecedorId);

    List<NotaFiscal> findByClienteId(Long clienteId);
}
