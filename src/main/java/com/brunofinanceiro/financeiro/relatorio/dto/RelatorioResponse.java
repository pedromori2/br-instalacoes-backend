package com.brunofinanceiro.financeiro.relatorio.dto;

import com.brunofinanceiro.financeiro.lancamento.dto.LancamentoResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RelatorioResponse(
        LocalDate dataInicio,
        LocalDate dataFim,
        BigDecimal totalEntradas,
        BigDecimal totalSaidas,
        BigDecimal saldo,
        List<CategoriaTotalResponse> porCategoria,
        List<LancamentoResponse> lancamentos
) {
}
