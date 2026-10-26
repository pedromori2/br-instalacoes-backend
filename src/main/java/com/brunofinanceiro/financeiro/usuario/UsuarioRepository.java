package com.brunofinanceiro.financeiro.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findBySupabaseUserId(UUID supabaseUserId);

    boolean existsByEmail(String email);
}
