package com.brunofinanceiro.financeiro.usuario;

import com.brunofinanceiro.financeiro.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

/**
 * Fala com a Supabase Admin API para criar/atualizar usuarios no Supabase Auth, usando a
 * service_role key (nunca exposta ao frontend). E aqui que a senha de fato e gravada - o backend
 * nunca armazena nem processa a senha em texto puro ou hash.
 */
@Component
public class SupabaseAdminClient {

    private final RestClient restClient;

    public SupabaseAdminClient(@Value("${supabase.url}") String supabaseUrl,
                                @Value("${supabase.service-role-key}") String serviceRoleKey) {
        this.restClient = RestClient.builder()
                .baseUrl(supabaseUrl + "/auth/v1/admin")
                .defaultHeader("apikey", serviceRoleKey)
                .defaultHeader("Authorization", "Bearer " + serviceRoleKey)
                .build();
    }

    /** Cria o usuario no Supabase Auth (ja confirmado, sem precisar de e-mail de verificacao) e retorna o UUID gerado. */
    public UUID criarUsuario(String email, String senha) {
        try {
            Map<String, Object> response = restClient.post()
                    .uri("/users")
                    .body(Map.of("email", email, "password", senha, "email_confirm", true))
                    .retrieve()
                    .body(Map.class);
            String id = (String) response.get("id");
            return UUID.fromString(id);
        } catch (Exception ex) {
            throw new BusinessException("Nao foi possivel criar o usuario no Supabase: " + ex.getMessage());
        }
    }

    public void atualizarSenha(UUID supabaseUserId, String novaSenha) {
        try {
            restClient.put()
                    .uri("/users/{id}", supabaseUserId)
                    .body(Map.of("password", novaSenha))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            throw new BusinessException("Nao foi possivel atualizar a senha no Supabase: " + ex.getMessage());
        }
    }
}
