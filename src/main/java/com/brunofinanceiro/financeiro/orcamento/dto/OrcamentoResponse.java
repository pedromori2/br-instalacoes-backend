package com.brunofinanceiro.financeiro.orcamento.dto;

import com.brunofinanceiro.financeiro.orcamento.Orcamento;
import com.brunofinanceiro.financeiro.orcamento.StatusOrcamento;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record OrcamentoResponse(
        Long id,
        Long clienteId,
        String clienteNome,
        LocalDate dataOrcamento,
        LocalDate dataValidade,
        StatusOrcamento status,
        String observacoes,
        BigDecimal valorTotal,
        String usuarioNome,
        Instant createdAt,
        List<OrcamentoItemResponse> itens
) {
    public static OrcamentoResponse from(Orcamento o) {
        return new OrcamentoResponse(
                o.getId(),
                o.getCliente().getId(),
                o.getCliente().getNome(),
                o.getDataOrcamento(),
                o.getDataValidade(),
                o.getStatus(),
                o.getObservacoes(),
                o.getValorTotal(),
                o.getUsuario().getNome(),
                o.getCreatedAt(),
                o.getItens().stream().map(OrcamentoItemResponse::from).toList()
        );
    }
}
