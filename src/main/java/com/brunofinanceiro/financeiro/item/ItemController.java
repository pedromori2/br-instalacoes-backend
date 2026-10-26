package com.brunofinanceiro.financeiro.item;

import com.brunofinanceiro.financeiro.item.dto.ItemRequest;
import com.brunofinanceiro.financeiro.item.dto.ItemResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itens")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemResponse> listar(@RequestParam(required = false) TipoItem tipo) {
        return itemService.listarTodos(tipo).stream().map(ItemResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ItemResponse buscar(@PathVariable Long id) {
        return ItemResponse.from(itemService.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse criar(@Valid @RequestBody ItemRequest request) {
        return ItemResponse.from(itemService.criar(request));
    }

    @PutMapping("/{id}")
    public ItemResponse atualizar(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        return ItemResponse.from(itemService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(@PathVariable Long id) {
        itemService.excluir(id);
    }
}
