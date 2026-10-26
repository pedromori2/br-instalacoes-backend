package com.brunofinanceiro.financeiro.item;

import com.brunofinanceiro.financeiro.common.exception.ResourceNotFoundException;
import com.brunofinanceiro.financeiro.item.dto.ItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public List<Item> listarTodos(TipoItem tipo) {
        return tipo == null ? itemRepository.findAll() : itemRepository.findByTipo(tipo);
    }

    @Transactional(readOnly = true)
    public Item buscarPorId(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Item", id));
    }

    @Transactional
    public Item criar(ItemRequest request) {
        Item item = Item.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .tipo(request.tipo())
                .precoUnitario(request.precoUnitario())
                .unidade(request.unidade() == null || request.unidade().isBlank() ? "un" : request.unidade())
                .ativo(request.ativo() == null || request.ativo())
                .build();
        return itemRepository.save(item);
    }

    @Transactional
    public Item atualizar(Long id, ItemRequest request) {
        Item item = buscarPorId(id);
        item.setNome(request.nome());
        item.setDescricao(request.descricao());
        item.setTipo(request.tipo());
        item.setPrecoUnitario(request.precoUnitario());
        item.setUnidade(request.unidade() == null || request.unidade().isBlank() ? "un" : request.unidade());
        if (request.ativo() != null) {
            item.setAtivo(request.ativo());
        }
        return itemRepository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        Item item = buscarPorId(id);
        itemRepository.delete(item);
    }
}
