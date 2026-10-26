package com.brunofinanceiro.financeiro.orcamento.dto;

import com.brunofinanceiro.financeiro.orcamento.TipoDesconto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrcamentoItemRequest(
        @NotNull(message = "Item (produto/servico) e obrigatorio") Long itemId,
        String descricaoPersonalizada,
        @NotNull(message = "Quantidade e obrigatoria") @DecimalMin(value = "0.001", message = "Quantidade deve ser maior que zero") BigDecimal quantidade,
        BigDecimal valorUnitario,
        TipoDesconto tipoDesconto,
        BigDecimal valorDesconto
) {
}
