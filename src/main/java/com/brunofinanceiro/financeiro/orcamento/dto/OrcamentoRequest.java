package com.brunofinanceiro.financeiro.orcamento.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record OrcamentoRequest(
        @NotNull(message = "Cliente e obrigatorio") Long clienteId,
        @NotNull(message = "Data do orcamento e obrigatoria") LocalDate dataOrcamento,
        LocalDate dataValidade,
        String observacoes,
        List<OrcamentoItemRequest> itens
) {
}
