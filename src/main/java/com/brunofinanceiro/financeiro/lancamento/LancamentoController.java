package com.brunofinanceiro.financeiro.lancamento;

import com.brunofinanceiro.financeiro.categoria.TipoLancamento;
import com.brunofinanceiro.financeiro.lancamento.dto.LancamentoRequest;
import com.brunofinanceiro.financeiro.lancamento.dto.LancamentoResponse;
import com.brunofinanceiro.financeiro.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

    private final LancamentoService lancamentoService;

    @GetMapping
    public List<LancamentoResponse> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) TipoLancamento tipo,
            @RequestParam(required = false) Long categoriaId) {
        return lancamentoService.listarComFiltros(dataInicio, dataFim, tipo, categoriaId)
                .stream().map(LancamentoResponse::from).toList();
    }

    @GetMapping("/{id}")
    public LancamentoResponse buscar(@PathVariable Long id) {
        return LancamentoResponse.from(lancamentoService.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LancamentoResponse criar(@Valid @RequestBody LancamentoRequest request,
                                     @AuthenticationPrincipal UserPrincipal principal) {
        return LancamentoResponse.from(lancamentoService.criar(request, principal.getId()));
    }

    @PutMapping("/{id}")
    public LancamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody LancamentoRequest request) {
        return LancamentoResponse.from(lancamentoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(@PathVariable Long id) {
        lancamentoService.excluir(id);
    }
}
