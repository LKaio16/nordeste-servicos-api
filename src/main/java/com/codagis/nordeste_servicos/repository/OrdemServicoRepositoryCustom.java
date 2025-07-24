package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.model.StatusOS;

import java.util.List;

public interface OrdemServicoRepositoryCustom {
    List<OrdemServico> findByFilters(Long tecnicoId, Long clienteId, StatusOS status, String searchTerm);
} 