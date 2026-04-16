package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.dto.ContaListItemDTO;
import com.codagis.nordeste_servicos.model.StatusConta;
import com.codagis.nordeste_servicos.model.TipoConta;

import java.util.List;

public interface ContaRepositoryCustom {
    List<ContaListItemDTO> findListItemsByFilters(Long clienteId, Long fornecedorId, TipoConta tipo, StatusConta status, int page, int size);
    long countByFilters(Long clienteId, Long fornecedorId, TipoConta tipo, StatusConta status);
}
