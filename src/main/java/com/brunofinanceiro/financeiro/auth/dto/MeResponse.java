package com.brunofinanceiro.financeiro.auth.dto;

import com.brunofinanceiro.financeiro.security.UserPrincipal;
import com.brunofinanceiro.financeiro.usuario.Role;

public record MeResponse(
        Long id,
        String nome,
        String email,
        Role role
) {
    public static MeResponse from(UserPrincipal principal) {
        return new MeResponse(
                principal.getId(),
                principal.getNome(),
                principal.getUsername(),
                principal.getUsuario().getRole()
        );
    }
}
