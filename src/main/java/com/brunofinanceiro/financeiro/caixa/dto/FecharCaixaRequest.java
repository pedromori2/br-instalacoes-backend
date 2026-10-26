package com.brunofinanceiro.financeiro.caixa.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FecharCaixaRequest(
        @NotNull(message = "Saldo final informado (contado) e obrigatorio") BigDecimal saldoFinalInformado,
        String observacoes
) {
}
