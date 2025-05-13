package com.codagis.nordeste_servicos.model;

public enum StatusOS {
    EM_ABERTO,
    ATRIBUIDA,
    EM_ANDAMENTO,
    PENDENTE_PECAS,
    AGUARDANDO_APROVACAO, // Se houver fluxo de aprovação
    CONCLUIDA,
    ENCERRADA, // Finalizada e processada
    CANCELADA
}