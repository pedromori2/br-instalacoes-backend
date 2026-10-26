package com.brunofinanceiro.financeiro.caixa.dto;

import com.brunofinanceiro.financeiro.caixa.FechamentoCaixa;
import com.brunofinanceiro.financeiro.caixa.StatusFechamento;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record FechamentoResponse(
        Long id,
        LocalDate dataFechamento,
        BigDecimal saldoInicial,
        BigDecimal totalEntradas,
        BigDecimal totalSaidas,
        BigDecimal saldoFinalCalculado,
        BigDecimal saldoFinalInformado,
        BigDecimal diferenca,
        StatusFechamento status,
        String observacoes,
        String usuarioNome,
        Instant dataHoraFechamento
) {
    public static FechamentoResponse from(FechamentoCaixa f) {
        return new FechamentoResponse(
                f.getId(),
                f.getDataFechamento(),
                f.getSaldoInicial(),
                f.getTotalEntradas(),
                f.getTotalSaidas(),
                f.getSaldoFinalCalculado(),
                f.getSaldoFinalInformado(),
                f.getDiferenca(),
                f.getStatus(),
                f.getObservacoes(),
                f.getUsuario() != null ? f.getUsuario().getNome() : null,
                f.getDataHoraFechamento()
        );
    }

    public static FechamentoResponse preview(LocalDate data, BigDecimal saldoInicial, BigDecimal totalEntradas,
                                               BigDecimal totalSaidas, BigDecimal saldoFinalCalculado) {
        return new FechamentoResponse(
                null, data, saldoInicial, totalEntradas, totalSaidas, saldoFinalCalculado,
                null, null, StatusFechamento.ABERTO, null, null, null
        );
    }
}
