package com.brunofinanceiro.financeiro.lancamento;

import com.brunofinanceiro.financeiro.categoria.CategoriaLancamento;
import com.brunofinanceiro.financeiro.categoria.CategoriaLancamentoRepository;
import com.brunofinanceiro.financeiro.categoria.TipoLancamento;
import com.brunofinanceiro.financeiro.common.exception.BusinessException;
import com.brunofinanceiro.financeiro.common.exception.ResourceNotFoundException;
import com.brunofinanceiro.financeiro.lancamento.dto.LancamentoRequest;
import com.brunofinanceiro.financeiro.orcamento.Orcamento;
import com.brunofinanceiro.financeiro.usuario.Usuario;
import com.brunofinanceiro.financeiro.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;
    private final CategoriaLancamentoRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Lancamento> listarComFiltros(LocalDate dataInicio, LocalDate dataFim, TipoLancamento tipo, Long categoriaId) {
        return lancamentoRepository.findAll(
                LancamentoSpecifications.comFiltros(dataInicio, dataFim, tipo, categoriaId));
    }

    @Transactional(readOnly = true)
    public Lancamento buscarPorId(Long id) {
        return lancamentoRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Lancamento", id));
    }

    @Transactional(readOnly = true)
    public List<Lancamento> listarPorData(LocalDate data) {
        return lancamentoRepository.findByDataLancamento(data);
    }

    @Transactional
    public Lancamento criar(LancamentoRequest request, Long usuarioId) {
        CategoriaLancamento categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> ResourceNotFoundException.of("Categoria", request.categoriaId()));
        if (categoria.getTipo() != request.tipo()) {
            throw new BusinessException("A categoria selecionada nao e compativel com o tipo de lancamento (ENTRADA/SAIDA)");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", usuarioId));

        Lancamento lancamento = Lancamento.builder()
                .tipo(request.tipo())
                .categoria(categoria)
                .descricao(request.descricao())
                .valor(request.valor())
                .dataLancamento(request.dataLancamento())
                .formaPagamento(request.formaPagamento())
                .usuario(usuario)
                .build();
        return lancamentoRepository.save(lancamento);
    }

    @Transactional
    public Lancamento atualizar(Long id, LancamentoRequest request) {
        Lancamento lancamento = buscarPorId(id);
        garantirNaoTravado(lancamento);

        CategoriaLancamento categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> ResourceNotFoundException.of("Categoria", request.categoriaId()));
        if (categoria.getTipo() != request.tipo()) {
            throw new BusinessException("A categoria selecionada nao e compativel com o tipo de lancamento (ENTRADA/SAIDA)");
        }

        lancamento.setTipo(request.tipo());
        lancamento.setCategoria(categoria);
        lancamento.setDescricao(request.descricao());
        lancamento.setValor(request.valor());
        lancamento.setDataLancamento(request.dataLancamento());
        lancamento.setFormaPagamento(request.formaPagamento());
        return lancamentoRepository.save(lancamento);
    }

    @Transactional
    public void excluir(Long id) {
        Lancamento lancamento = buscarPorId(id);
        garantirNaoTravado(lancamento);
        lancamentoRepository.delete(lancamento);
    }

    private void garantirNaoTravado(Lancamento lancamento) {
        if (lancamento.isTravadoPorFechamento()) {
            throw new BusinessException("Este lancamento pertence a um fechamento de caixa e nao pode ser alterado/excluido");
        }
    }

    /**
     * Usado pelo modulo de orcamento ao aprovar um orcamento: gera a entrada
     * correspondente de forma idempotente (nao duplica se o orcamento ja possuir lancamento).
     */
    @Transactional
    public Lancamento criarEntradaDeOrcamento(Orcamento orcamento, CategoriaLancamento categoria, BigDecimal valor,
                                               FormaPagamento formaPagamento, Usuario usuario) {
        if (lancamentoRepository.existsByOrcamentoId(orcamento.getId())) {
            throw new BusinessException("Este orcamento ja possui um lancamento de entrada vinculado");
        }
        Lancamento lancamento = Lancamento.builder()
                .tipo(TipoLancamento.ENTRADA)
                .categoria(categoria)
                .descricao("Recebimento do orcamento #" + orcamento.getId() + " - " + orcamento.getCliente().getNome())
                .valor(valor)
                .dataLancamento(LocalDate.now())
                .formaPagamento(formaPagamento)
                .orcamento(orcamento)
                .usuario(usuario)
                .build();
        return lancamentoRepository.save(lancamento);
    }
}
