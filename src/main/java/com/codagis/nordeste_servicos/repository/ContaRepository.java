package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.Conta;
import com.codagis.nordeste_servicos.model.StatusConta;
import com.codagis.nordeste_servicos.model.TipoConta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    List<Conta> findByTipo(TipoConta tipo);

    List<Conta> findByStatus(StatusConta status);

    List<Conta> findByClienteId(Long clienteId);

    List<Conta> findByFornecedorId(Long fornecedorId);

    List<Conta> findByDataVencimentoBetween(LocalDate dataInicio, LocalDate dataFim);

    List<Conta> findByTipoAndStatus(TipoConta tipo, StatusConta status);
}
