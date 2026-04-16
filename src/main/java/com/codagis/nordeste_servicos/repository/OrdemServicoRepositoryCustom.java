package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.model.OrdemServico;
import com.codagis.nordeste_servicos.model.StatusOS;
import com.codagis.nordeste_servicos.dto.OrdemServicoListItemDTO;

import java.util.List;

public interface OrdemServicoRepositoryCustom {
    List<OrdemServico> findByFilters(Long tecnicoId, Long clienteId, StatusOS status, String searchTerm);
    List<OrdemServico> findByFilters(Long tecnicoId, Long clienteId, StatusOS status, String searchTerm, int page, int size);
    List<OrdemServicoListItemDTO> findListItemsByFilters(Long tecnicoId, Long clienteId, StatusOS status, String searchTerm, int page, int size);
    long countByFilters(Long tecnicoId, Long clienteId, StatusOS status, String searchTerm);
} 