package com.brunofinanceiro.financeiro.cliente.dto;

import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
        @NotBlank(message = "Nome e obrigatorio") String nome,
        String documento,
        String email,
        String telefone,
        String endereco,
        String observacoes,
        Boolean ativo
) {
}
