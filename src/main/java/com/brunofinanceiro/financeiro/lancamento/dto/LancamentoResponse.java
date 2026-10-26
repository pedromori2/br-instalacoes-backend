package com.brunofinanceiro.financeiro.lancamento.dto;

import com.brunofinanceiro.financeiro.categoria.TipoLancamento;
import com.brunofinanceiro.financeiro.lancamento.FormaPagamento;
import com.brunofinanceiro.financeiro.lancamento.Lancamento;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record LancamentoResponse(
        Long id,
        TipoLancamento tipo,
        Long categoriaId,
        String categoriaNome,
        String descricao,
        BigDecimal valor,
        LocalDate dataLancamento,
        FormaPagamento formaPagamento,
        Long orcamentoId,
        Long fechamentoCaixaId,
        boolean travadoPorFechamento,
        String usuarioNome,
        Instant createdAt
) {
    public static LancamentoResponse from(Lancamento l) {
        return new LancamentoResponse(
                l.getId(),
                l.getTipo(),
                l.getCategoria().getId(),
                l.getCategoria().getNome(),
                l.getDescricao(),
                l.getValor(),
                l.getDataLancamento(),
                l.getFormaPagamento(),
                l.getOrcamento() != null ? l.getOrcamento().getId() : null,
                l.getFechamentoCaixa() != null ? l.getFechamentoCaixa().getId() : null,
                l.isTravadoPorFechamento(),
                l.getUsuario().getNome(),
                l.getCreatedAt()
        );
    }
}
