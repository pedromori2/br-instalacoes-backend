package com.brunofinanceiro.financeiro.orcamento;

import com.brunofinanceiro.financeiro.orcamento.dto.OrcamentoItemRequest;
import com.brunofinanceiro.financeiro.orcamento.dto.OrcamentoRequest;
import com.brunofinanceiro.financeiro.orcamento.dto.OrcamentoResponse;
import com.brunofinanceiro.financeiro.orcamento.dto.StatusUpdateRequest;
import com.brunofinanceiro.financeiro.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orcamentos")
@RequiredArgsConstructor
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    @GetMapping
    public List<OrcamentoResponse> listar(@RequestParam(required = false) StatusOrcamento status) {
        return orcamentoService.listarTodos(status).stream().map(OrcamentoResponse::from).toList();
    }

    @GetMapping("/{id}")
    public OrcamentoResponse buscar(@PathVariable Long id) {
        return OrcamentoResponse.from(orcamentoService.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrcamentoResponse criar(@Valid @RequestBody OrcamentoRequest request,
                                    @AuthenticationPrincipal UserPrincipal principal) {
        return OrcamentoResponse.from(orcamentoService.criar(request, principal.getId()));
    }

    @PutMapping("/{id}")
    public OrcamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody OrcamentoRequest request) {
        return OrcamentoResponse.from(orcamentoService.atualizarHeader(id, request));
    }

    @PostMapping("/{id}/itens")
    public OrcamentoResponse adicionarItem(@PathVariable Long id, @Valid @RequestBody OrcamentoItemRequest request) {
        return OrcamentoResponse.from(orcamentoService.adicionarItem(id, request));
    }

    @PutMapping("/{id}/itens/{itemId}")
    public OrcamentoResponse atualizarItem(@PathVariable Long id, @PathVariable Long itemId,
                                            @Valid @RequestBody OrcamentoItemRequest request) {
        return OrcamentoResponse.from(orcamentoService.atualizarItem(id, itemId, request));
    }

    @DeleteMapping("/{id}/itens/{itemId}")
    public OrcamentoResponse removerItem(@PathVariable Long id, @PathVariable Long itemId) {
        return OrcamentoResponse.from(orcamentoService.removerItem(id, itemId));
    }

    @PatchMapping("/{id}/status")
    public OrcamentoResponse atualizarStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        return OrcamentoResponse.from(orcamentoService.atualizarStatus(id, request, principal.getId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(@PathVariable Long id) {
        orcamentoService.excluir(id);
    }
}
