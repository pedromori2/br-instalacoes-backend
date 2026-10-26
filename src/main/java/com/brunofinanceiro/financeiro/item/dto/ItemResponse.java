package com.brunofinanceiro.financeiro.item.dto;

import com.brunofinanceiro.financeiro.item.Item;
import com.brunofinanceiro.financeiro.item.TipoItem;

import java.math.BigDecimal;
import java.time.Instant;

public record ItemResponse(
        Long id,
        String nome,
        String descricao,
        TipoItem tipo,
        BigDecimal precoUnitario,
        String unidade,
        boolean ativo,
        Instant createdAt
) {
    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getNome(),
                item.getDescricao(),
                item.getTipo(),
                item.getPrecoUnitario(),
                item.getUnidade(),
                item.isAtivo(),
                item.getCreatedAt()
        );
    }
}
