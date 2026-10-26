package com.brunofinanceiro.financeiro.usuario.dto;

import com.brunofinanceiro.financeiro.usuario.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCreateRequest(
        @NotBlank(message = "Nome e obrigatorio") String nome,
        @NotBlank(message = "Email e obrigatorio") @Email(message = "Email invalido") String email,
        @NotBlank(message = "Senha e obrigatoria") @Size(min = 6, message = "Senha deve ter ao menos 6 caracteres") String senha,
        @NotNull(message = "Papel (role) e obrigatorio") Role role
) {
}
