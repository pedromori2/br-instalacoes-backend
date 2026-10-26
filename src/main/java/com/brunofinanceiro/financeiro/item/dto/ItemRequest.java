package com.brunofinanceiro.financeiro.item.dto;

import com.brunofinanceiro.financeiro.item.TipoItem;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ItemRequest(
        @NotBlank(message = "Nome e obrigatorio") String nome,
        String descricao,
        @NotNull(message = "Tipo e obrigatorio") TipoItem tipo,
        @NotNull(message = "Preco unitario e obrigatorio") @DecimalMin(value = "0.0", inclusive = true, message = "Preco nao pode ser negativo") BigDecimal precoUnitario,
        String unidade,
        Boolean ativo
) {
}
