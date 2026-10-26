package com.brunofinanceiro.financeiro.auth;

import com.brunofinanceiro.financeiro.auth.dto.MeResponse;
import com.brunofinanceiro.financeiro.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * O login em si acontece direto no Supabase Auth (frontend fala com o Supabase, nao com este
 * backend). Este endpoint so devolve o perfil de negocio (papel, nome) do usuario ja autenticado,
 * para o frontend popular a sessao logo apos o login.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return MeResponse.from(principal);
    }
}
