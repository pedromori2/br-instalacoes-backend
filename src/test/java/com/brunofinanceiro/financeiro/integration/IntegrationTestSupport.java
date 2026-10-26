package com.brunofinanceiro.financeiro.integration;

import com.brunofinanceiro.financeiro.usuario.Usuario;
import com.brunofinanceiro.financeiro.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * Metodos auxiliares comuns aos testes de integracao. O JWT de teste e assinado e validado pelo
 * par de chaves de TestSecurityConfig, que substitui o JwtDecoder baseado em JWKS do Supabase
 * usado em producao - com o "sub" apontando para o usuario admin semeado pela migration V2, sem
 * precisar de rede nem de um projeto Supabase real.
 */
public abstract class IntegrationTestSupport {

    protected static final String ADMIN_EMAIL = "admin@financeiro.local";
    private static final UUID ADMIN_SUPABASE_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Autowired
    private UsuarioRepository usuarioRepository;

    protected String autenticar(TestRestTemplate restTemplate) {
        Usuario admin = usuarioRepository.findByEmail(ADMIN_EMAIL).orElseThrow();
        if (!ADMIN_SUPABASE_USER_ID.equals(admin.getSupabaseUserId())) {
            admin.setSupabaseUserId(ADMIN_SUPABASE_USER_ID);
            usuarioRepository.save(admin);
        }
        return TestSecurityConfig.mintSupabaseJwt(ADMIN_SUPABASE_USER_ID);
    }

    protected HttpEntity<Object> autenticado(Object body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    protected <T> ResponseEntity<T> exchange(TestRestTemplate restTemplate, String url, HttpMethod method,
                                              Object body, String token, Class<T> responseType) {
        return restTemplate.exchange(url, method, autenticado(body, token), responseType);
    }
}
