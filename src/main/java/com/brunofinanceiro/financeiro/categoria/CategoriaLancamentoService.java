package com.brunofinanceiro.financeiro.categoria;

import com.brunofinanceiro.financeiro.categoria.dto.CategoriaRequest;
import com.brunofinanceiro.financeiro.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaLancamentoService {

    private final CategoriaLancamentoRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaLancamento> listarTodas(TipoLancamento tipo) {
        return tipo == null ? categoriaRepository.findAll() : categoriaRepository.findByTipo(tipo);
    }

    @Transactional(readOnly = true)
    public CategoriaLancamento buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Categoria", id));
    }

    @Transactional
    public CategoriaLancamento criar(CategoriaRequest request) {
        CategoriaLancamento categoria = CategoriaLancamento.builder()
                .nome(request.nome())
                .tipo(request.tipo())
                .descricao(request.descricao())
                .build();
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public CategoriaLancamento atualizar(Long id, CategoriaRequest request) {
        CategoriaLancamento categoria = buscarPorId(id);
        categoria.setNome(request.nome());
        categoria.setTipo(request.tipo());
        categoria.setDescricao(request.descricao());
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void excluir(Long id) {
        categoriaRepository.delete(buscarPorId(id));
    }
}
