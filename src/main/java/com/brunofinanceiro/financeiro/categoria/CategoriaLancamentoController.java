package com.brunofinanceiro.financeiro.categoria;

import com.brunofinanceiro.financeiro.categoria.dto.CategoriaRequest;
import com.brunofinanceiro.financeiro.categoria.dto.CategoriaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaLancamentoController {

    private final CategoriaLancamentoService categoriaService;

    @GetMapping
    public List<CategoriaResponse> listar(@RequestParam(required = false) TipoLancamento tipo) {
        return categoriaService.listarTodas(tipo).stream().map(CategoriaResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaResponse criar(@Valid @RequestBody CategoriaRequest request) {
        return CategoriaResponse.from(categoriaService.criar(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaResponse atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return CategoriaResponse.from(categoriaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(@PathVariable Long id) {
        categoriaService.excluir(id);
    }
}
