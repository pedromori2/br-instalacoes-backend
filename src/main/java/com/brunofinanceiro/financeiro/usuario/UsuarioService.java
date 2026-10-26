package com.brunofinanceiro.financeiro.usuario;

import com.brunofinanceiro.financeiro.common.exception.BusinessException;
import com.brunofinanceiro.financeiro.common.exception.ResourceNotFoundException;
import com.brunofinanceiro.financeiro.usuario.dto.UsuarioCreateRequest;
import com.brunofinanceiro.financeiro.usuario.dto.UsuarioUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SupabaseAdminClient supabaseAdminClient;

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", id));
    }

    @Transactional
    public Usuario criar(UsuarioCreateRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("Ja existe um usuario cadastrado com este email");
        }
        // Cria no Supabase Auth primeiro: so gravamos o registro local se a conta foi criada com sucesso.
        UUID supabaseUserId = supabaseAdminClient.criarUsuario(request.email(), request.senha());

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .supabaseUserId(supabaseUserId)
                .role(request.role())
                .ativo(true)
                .build();
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = buscarPorId(id);
        usuario.setNome(request.nome());
        usuario.setRole(request.role());
        usuario.setAtivo(request.ativo());
        if (request.novaSenha() != null && !request.novaSenha().isBlank()) {
            if (usuario.getSupabaseUserId() == null) {
                throw new BusinessException("Este usuario ainda nao esta vinculado ao Supabase - nao e possivel trocar a senha por aqui");
            }
            supabaseAdminClient.atualizarSenha(usuario.getSupabaseUserId(), request.novaSenha());
        }
        return usuarioRepository.save(usuario);
    }
}
