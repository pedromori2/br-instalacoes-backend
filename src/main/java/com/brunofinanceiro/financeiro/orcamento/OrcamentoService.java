package com.brunofinanceiro.financeiro.orcamento;

import com.brunofinanceiro.financeiro.categoria.CategoriaLancamento;
import com.brunofinanceiro.financeiro.categoria.CategoriaLancamentoRepository;
import com.brunofinanceiro.financeiro.categoria.TipoLancamento;
import com.brunofinanceiro.financeiro.cliente.Cliente;
import com.brunofinanceiro.financeiro.cliente.ClienteRepository;
import com.brunofinanceiro.financeiro.common.exception.BusinessException;
import com.brunofinanceiro.financeiro.common.exception.ResourceNotFoundException;
import com.brunofinanceiro.financeiro.item.Item;
import com.brunofinanceiro.financeiro.item.ItemRepository;
import com.brunofinanceiro.financeiro.lancamento.LancamentoService;
import com.brunofinanceiro.financeiro.orcamento.dto.OrcamentoItemRequest;
import com.brunofinanceiro.financeiro.orcamento.dto.OrcamentoRequest;
import com.brunofinanceiro.financeiro.orcamento.dto.StatusUpdateRequest;
import com.brunofinanceiro.financeiro.usuario.Usuario;
import com.brunofinanceiro.financeiro.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrcamentoService {

    private static final String CATEGORIA_RECEBIMENTO_ORCAMENTO = "Recebimento de Orcamento";

    private static final Map<StatusOrcamento, Set<StatusOrcamento>> TRANSICOES_PERMITIDAS = Map.of(
            StatusOrcamento.RASCUNHO, Set.of(StatusOrcamento.ENVIADO, StatusOrcamento.CANCELADO),
            StatusOrcamento.ENVIADO, Set.of(StatusOrcamento.APROVADO, StatusOrcamento.REJEITADO, StatusOrcamento.CANCELADO),
            StatusOrcamento.APROVADO, Set.of(StatusOrcamento.FINALIZADO, StatusOrcamento.CANCELADO),
            StatusOrcamento.FINALIZADO, Set.of(),
            StatusOrcamento.REJEITADO, Set.of(),
            StatusOrcamento.CANCELADO, Set.of()
    );

    private final OrcamentoRepository orcamentoRepository;
    private final ClienteRepository clienteRepository;
    private final ItemRepository itemRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaLancamentoRepository categoriaLancamentoRepository;
    private final LancamentoService lancamentoService;

    @Transactional(readOnly = true)
    public List<Orcamento> listarTodos(StatusOrcamento status) {
        return status == null ? orcamentoRepository.findAll() : orcamentoRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public Orcamento buscarPorId(Long id) {
        return orcamentoRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Orcamento", id));
    }

    @Transactional
    public Orcamento criar(OrcamentoRequest request, Long usuarioId) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> ResourceNotFoundException.of("Cliente", request.clienteId()));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", usuarioId));

        Orcamento orcamento = Orcamento.builder()
                .cliente(cliente)
                .dataOrcamento(request.dataOrcamento())
                .dataValidade(request.dataValidade())
                .observacoes(request.observacoes())
                .status(StatusOrcamento.RASCUNHO)
                .valorTotal(BigDecimal.ZERO)
                .usuario(usuario)
                .build();

        if (request.itens() != null) {
            for (OrcamentoItemRequest itemRequest : request.itens()) {
                orcamento.adicionarItem(construirItem(itemRequest));
            }
        }
        orcamento.recalcularValorTotal();
        return orcamentoRepository.save(orcamento);
    }

    @Transactional
    public Orcamento atualizarHeader(Long id, OrcamentoRequest request) {
        Orcamento orcamento = buscarPorId(id);
        garantirRascunho(orcamento);

        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> ResourceNotFoundException.of("Cliente", request.clienteId()));

        orcamento.setCliente(cliente);
        orcamento.setDataOrcamento(request.dataOrcamento());
        orcamento.setDataValidade(request.dataValidade());
        orcamento.setObservacoes(request.observacoes());
        return orcamentoRepository.save(orcamento);
    }

    @Transactional
    public Orcamento adicionarItem(Long orcamentoId, OrcamentoItemRequest request) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        garantirRascunho(orcamento);
        orcamento.adicionarItem(construirItem(request));
        orcamento.recalcularValorTotal();
        return orcamentoRepository.save(orcamento);
    }

    @Transactional
    public Orcamento atualizarItem(Long orcamentoId, Long orcamentoItemId, OrcamentoItemRequest request) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        garantirRascunho(orcamento);

        OrcamentoItem item = orcamento.getItens().stream()
                .filter(i -> i.getId().equals(orcamentoItemId))
                .findFirst()
                .orElseThrow(() -> ResourceNotFoundException.of("Item do orcamento", orcamentoItemId));

        Item itemCadastro = itemRepository.findById(request.itemId())
                .orElseThrow(() -> ResourceNotFoundException.of("Item", request.itemId()));

        item.setItem(itemCadastro);
        item.setDescricaoPersonalizada(request.descricaoPersonalizada());
        item.setQuantidade(request.quantidade());
        item.setValorUnitario(request.valorUnitario() != null ? request.valorUnitario() : itemCadastro.getPrecoUnitario());
        item.setTipoDesconto(request.tipoDesconto() != null ? request.tipoDesconto() : TipoDesconto.NENHUM);
        item.setValorDesconto(request.valorDesconto() != null ? request.valorDesconto() : BigDecimal.ZERO);
        item.recalcularValorTotal();

        orcamento.recalcularValorTotal();
        return orcamentoRepository.save(orcamento);
    }

    @Transactional
    public Orcamento removerItem(Long orcamentoId, Long orcamentoItemId) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        garantirRascunho(orcamento);

        OrcamentoItem item = orcamento.getItens().stream()
                .filter(i -> i.getId().equals(orcamentoItemId))
                .findFirst()
                .orElseThrow(() -> ResourceNotFoundException.of("Item do orcamento", orcamentoItemId));

        orcamento.removerItem(item);
        orcamento.recalcularValorTotal();
        return orcamentoRepository.save(orcamento);
    }

    @Transactional
    public Orcamento atualizarStatus(Long orcamentoId, StatusUpdateRequest request, Long usuarioId) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        StatusOrcamento novoStatus = request.status();

        Set<StatusOrcamento> permitidos = TRANSICOES_PERMITIDAS.getOrDefault(orcamento.getStatus(), Set.of());
        if (!permitidos.contains(novoStatus)) {
            throw new BusinessException("Nao e possivel mudar o orcamento de " + orcamento.getStatus() + " para " + novoStatus);
        }

        if (novoStatus == StatusOrcamento.APROVADO) {
            if (orcamento.getItens().isEmpty() || orcamento.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("Orcamento precisa ter ao menos um item com valor para ser aprovado");
            }
        }

        if (novoStatus == StatusOrcamento.FINALIZADO) {
            if (request.formaPagamento() == null) {
                throw new BusinessException("Forma de pagamento e obrigatoria para finalizar o orcamento");
            }
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> ResourceNotFoundException.of("Usuario", usuarioId));
            CategoriaLancamento categoria = categoriaLancamentoRepository.findByTipo(TipoLancamento.ENTRADA).stream()
                    .filter(c -> c.getNome().equalsIgnoreCase(CATEGORIA_RECEBIMENTO_ORCAMENTO))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("Categoria padrao '" + CATEGORIA_RECEBIMENTO_ORCAMENTO + "' nao encontrada"));

            lancamentoService.criarEntradaDeOrcamento(orcamento, categoria, orcamento.getValorTotal(),
                    request.formaPagamento(), usuario);
        }

        orcamento.setStatus(novoStatus);
        return orcamentoRepository.save(orcamento);
    }

    @Transactional
    public void excluir(Long id) {
        Orcamento orcamento = buscarPorId(id);
        garantirRascunho(orcamento);
        orcamentoRepository.delete(orcamento);
    }

    private OrcamentoItem construirItem(OrcamentoItemRequest request) {
        Item item = itemRepository.findById(request.itemId())
                .orElseThrow(() -> ResourceNotFoundException.of("Item", request.itemId()));

        OrcamentoItem orcamentoItem = OrcamentoItem.builder()
                .item(item)
                .descricaoPersonalizada(request.descricaoPersonalizada())
                .quantidade(request.quantidade())
                .valorUnitario(request.valorUnitario() != null ? request.valorUnitario() : item.getPrecoUnitario())
                .tipoDesconto(request.tipoDesconto() != null ? request.tipoDesconto() : TipoDesconto.NENHUM)
                .valorDesconto(request.valorDesconto() != null ? request.valorDesconto() : BigDecimal.ZERO)
                .build();
        orcamentoItem.recalcularValorTotal();
        return orcamentoItem;
    }

    private void garantirRascunho(Orcamento orcamento) {
        if (orcamento.getStatus() != StatusOrcamento.RASCUNHO) {
            throw new BusinessException("Somente orcamentos em RASCUNHO podem ser editados");
        }
    }
}
