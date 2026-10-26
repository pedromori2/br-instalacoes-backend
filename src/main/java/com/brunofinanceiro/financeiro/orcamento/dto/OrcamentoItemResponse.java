package com.brunofinanceiro.financeiro.orcamento.dto;

import com.brunofinanceiro.financeiro.orcamento.OrcamentoItem;
import com.brunofinanceiro.financeiro.orcamento.TipoDesconto;

import java.math.BigDecimal;

public record OrcamentoItemResponse(
        Long id,
        Long itemId,
        String itemNome,
        String descricaoPersonalizada,
        BigDecimal quantidade,
        BigDecimal valorUnitario,
        TipoDesconto tipoDesconto,
        BigDecimal valorDesconto,
        BigDecimal valorTotal
) {
    public static OrcamentoItemResponse from(OrcamentoItem item) {
        return new OrcamentoItemResponse(
                item.getId(),
                item.getItem().getId(),
                item.getItem().getNome(),
                item.getDescricaoPersonalizada(),
                item.getQuantidade(),
                item.getValorUnitario(),
                item.getTipoDesconto(),
                item.getValorDesconto(),
                item.getValorTotal()
        );
    }
}
