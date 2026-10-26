package com.brunofinanceiro.financeiro.categoria.dto;

import com.brunofinanceiro.financeiro.categoria.TipoLancamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoriaRequest(
        @NotBlank(message = "Nome e obrigatorio") String nome,
        @NotNull(message = "Tipo e obrigatorio") TipoLancamento tipo,
        String descricao
) {
}
