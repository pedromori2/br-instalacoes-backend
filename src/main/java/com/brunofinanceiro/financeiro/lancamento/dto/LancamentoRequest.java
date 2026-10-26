package com.brunofinanceiro.financeiro.lancamento.dto;

import com.brunofinanceiro.financeiro.categoria.TipoLancamento;
import com.brunofinanceiro.financeiro.lancamento.FormaPagamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoRequest(
        @NotNull(message = "Tipo e obrigatorio") TipoLancamento tipo,
        @NotNull(message = "Categoria e obrigatoria") Long categoriaId,
        @NotBlank(message = "Descricao e obrigatoria") String descricao,
        @NotNull(message = "Valor e obrigatorio") @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero") BigDecimal valor,
        @NotNull(message = "Data e obrigatoria") LocalDate dataLancamento,
        @NotNull(message = "Forma de pagamento e obrigatoria") FormaPagamento formaPagamento
) {
}
