package com.brunofinanceiro.financeiro.usuario.dto;

import com.brunofinanceiro.financeiro.usuario.Role;
import com.brunofinanceiro.financeiro.usuario.Usuario;

import java.time.Instant;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Role role,
        boolean ativo,
        Instant createdAt
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.isAtivo(),
                usuario.getCreatedAt()
        );
    }
}
