package com.brunofinanceiro.financeiro.orcamento.dto;

import com.brunofinanceiro.financeiro.lancamento.FormaPagamento;
import com.brunofinanceiro.financeiro.orcamento.StatusOrcamento;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull(message = "Status e obrigatorio") StatusOrcamento status,
        // Obrigatorio apenas na transicao para FINALIZADO: define a forma de pagamento do
        // lancamento de entrada gerado automaticamente.
        FormaPagamento formaPagamento
) {
}
