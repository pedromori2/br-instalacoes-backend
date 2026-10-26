package com.brunofinanceiro.financeiro.relatorio.dto;

import com.brunofinanceiro.financeiro.categoria.TipoLancamento;

import java.math.BigDecimal;

public record CategoriaTotalResponse(
        Long categoriaId,
        String categoriaNome,
        TipoLancamento tipo,
        BigDecimal total
) {
}
