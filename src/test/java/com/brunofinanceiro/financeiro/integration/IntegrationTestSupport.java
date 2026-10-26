package com.brunofinanceiro.financeiro.integration;

import com.brunofinanceiro.financeiro.usuario.Usuario;
import com.brunofinanceiro.financeiro.usuario.UsuarioRepository;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Metodos auxiliares comuns aos testes de integracao. Como o login de verdade acontece no
 * Supabase Auth (fora deste backend), autenticar() aqui gera um JWT no mesmo formato HS256 que o
 * NimbusJwtDecoder do perfil "test" confia (ver application-test.yml), com o "sub" apontando para
 * o usuario admin semeado pela migration V2 - sem precisar de rede nem de um projeto Supabase real.
 */
public abstract class IntegrationTestSupport {

    protected static final String ADMIN_EMAIL = "admin@financeiro.local";
    private static final UUID ADMIN_SUPABASE_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${supabase.jwt-secret}")
    private String jwtSecret;

    protected String autenticar(TestRestTemplate restTemplate) {
        Usuario admin = usuarioRepository.findByEmail(ADMIN_EMAIL).orElseThrow();
        if (!ADMIN_SUPABASE_USER_ID.equals(admin.getSupabaseUserId())) {
            admin.setSupabaseUserId(ADMIN_SUPABASE_USER_ID);
            usuarioRepository.save(admin);
        }
        return mintSupabaseJwt(ADMIN_SUPABASE_USER_ID);
    }

    private String mintSupabaseJwt(UUID subject) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(subject.toString())
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime(Date.from(Instant.now().plusSeconds(3600)))
                    .claim("role", "authenticated")
                    .build();
            SignedJWT signedJwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            signedJwt.sign(new MACSigner(jwtSecret.getBytes(StandardCharsets.UTF_8)));
            return signedJwt.serialize();
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao gerar JWT de teste no formato Supabase", ex);
        }
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
