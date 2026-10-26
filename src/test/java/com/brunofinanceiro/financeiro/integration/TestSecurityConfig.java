package com.brunofinanceiro.financeiro.integration;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Em producao o Supabase assina os JWT com uma chave assimetrica propria, publicada via JWKS.
 * Aqui os testes geram seu proprio par de chaves e sobrescrevem o bean JwtDecoder (@Primary) para
 * confiar nele, assinando os tokens de teste com a chave privada correspondente - sem precisar de
 * rede nem de um projeto Supabase real. Aplicado automaticamente a todo @SpringBootTest via
 * META-INF/spring/org.springframework.boot.test.context.TestConfiguration.imports.
 */
@TestConfiguration
public class TestSecurityConfig {

    private static final KeyPair KEY_PAIR = gerarParDeChaves();

    @Bean
    @Primary
    public JwtDecoder testJwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withPublicKey((RSAPublicKey) KEY_PAIR.getPublic())
                .build();
        decoder.setJwtValidator(JwtValidators.createDefault());
        return decoder;
    }

    static String mintSupabaseJwt(UUID subject) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(subject.toString())
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime(Date.from(Instant.now().plusSeconds(3600)))
                    .claim("role", "authenticated")
                    .build();
            SignedJWT signedJwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
            signedJwt.sign(new RSASSASigner((RSAPrivateKey) KEY_PAIR.getPrivate()));
            return signedJwt.serialize();
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao gerar JWT de teste no formato Supabase", ex);
        }
    }

    private static KeyPair gerarParDeChaves() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
