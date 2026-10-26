package com.brunofinanceiro.financeiro.usuario.dto;

import com.brunofinanceiro.financeiro.usuario.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(
        @NotBlank(message = "Nome e obrigatorio") String nome,
        @NotNull(message = "Papel (role) e obrigatorio") Role role,
        boolean ativo,
        @Size(min = 6, message = "Senha deve ter ao menos 6 caracteres") String novaSenha
) {
}
