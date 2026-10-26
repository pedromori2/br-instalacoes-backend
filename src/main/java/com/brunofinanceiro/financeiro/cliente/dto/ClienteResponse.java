package com.brunofinanceiro.financeiro.cliente.dto;

import com.brunofinanceiro.financeiro.cliente.Cliente;

import java.time.Instant;

public record ClienteResponse(
        Long id,
        String nome,
        String documento,
        String email,
        String telefone,
        String endereco,
        String observacoes,
        boolean ativo,
        Instant createdAt
) {
    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getDocumento(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getEndereco(),
                cliente.getObservacoes(),
                cliente.isAtivo(),
                cliente.getCreatedAt()
        );
    }
}
