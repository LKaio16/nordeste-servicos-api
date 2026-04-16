package com.codagis.nordeste_servicos.repository;

import com.codagis.nordeste_servicos.dto.NotaFiscalListItemDTO;
import com.codagis.nordeste_servicos.model.TipoNotaFiscal;

import java.util.List;

public interface NotaFiscalRepositoryCustom {
    List<NotaFiscalListItemDTO> findListItemsByFilters(Long fornecedorId, Long clienteId, TipoNotaFiscal tipo, String searchTerm, int page, int size);
    long countByFilters(Long fornecedorId, Long clienteId, TipoNotaFiscal tipo, String searchTerm);
}
