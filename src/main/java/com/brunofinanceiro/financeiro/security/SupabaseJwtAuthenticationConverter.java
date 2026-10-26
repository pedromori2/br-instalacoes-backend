package com.brunofinanceiro.financeiro.security;

import com.brunofinanceiro.financeiro.usuario.Usuario;
import com.brunofinanceiro.financeiro.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Traduz um JWT ja validado (assinatura/expiracao) do Supabase Auth para a autenticacao usada em
 * todo o resto do backend: busca o usuario local pelo "sub" (UUID do Supabase) e embrulha no
 * UserPrincipal ja existente, para que @AuthenticationPrincipal UserPrincipal continue
 * funcionando sem mudar em nenhum controller.
 */
@Component
@RequiredArgsConstructor
public class SupabaseJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UsuarioRepository usuarioRepository;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        UUID supabaseUserId;
        try {
            supabaseUserId = UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException ex) {
            throw new InvalidBearerTokenException("Token sem identificador de usuario valido");
        }

        Usuario usuario = usuarioRepository.findBySupabaseUserId(supabaseUserId)
                .orElseThrow(() -> new InvalidBearerTokenException(
                        "Usuario autenticado no Supabase mas sem cadastro correspondente no sistema"));

        if (!usuario.isAtivo()) {
            throw new InvalidBearerTokenException("Usuario inativo");
        }

        UserPrincipal principal = new UserPrincipal(usuario);
        return new UsernamePasswordAuthenticationToken(principal, jwt, principal.getAuthorities());
    }
}
