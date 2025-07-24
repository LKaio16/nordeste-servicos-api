package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.Orcamento;
import com.codagis.nordeste_servicos.model.StatusOrcamento;
import java.util.List;

public interface OrcamentoRepositoryCustom {
    List<Orcamento> findByFilters(Long clienteId, StatusOrcamento status, Long ordemServicoOrigemId, String searchTerm);
} 