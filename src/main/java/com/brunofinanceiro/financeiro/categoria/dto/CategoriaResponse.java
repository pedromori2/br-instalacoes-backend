package com.brunofinanceiro.financeiro.categoria.dto;

import com.brunofinanceiro.financeiro.categoria.CategoriaLancamento;
import com.brunofinanceiro.financeiro.categoria.TipoLancamento;

public record CategoriaResponse(
        Long id,
        String nome,
        TipoLancamento tipo,
        String descricao
) {
    public static CategoriaResponse from(CategoriaLancamento categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNome(), categoria.getTipo(), categoria.getDescricao());
    }
}
