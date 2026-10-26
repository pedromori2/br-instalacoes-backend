package com.brunofinanceiro.financeiro.relatorio;

import com.brunofinanceiro.financeiro.categoria.TipoLancamento;
import com.brunofinanceiro.financeiro.lancamento.Lancamento;
import com.brunofinanceiro.financeiro.lancamento.LancamentoService;
import com.brunofinanceiro.financeiro.lancamento.dto.LancamentoResponse;
import com.brunofinanceiro.financeiro.relatorio.dto.CategoriaTotalResponse;
import com.brunofinanceiro.financeiro.relatorio.dto.RelatorioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final LancamentoService lancamentoService;

    @Transactional(readOnly = true)
    public RelatorioResponse gerarRelatorio(LocalDate dataInicio, LocalDate dataFim, TipoLancamento tipo, Long categoriaId) {
        List<Lancamento> lancamentos = lancamentoService.listarComFiltros(dataInicio, dataFim, tipo, categoriaId);

        BigDecimal totalEntradas = somar(lancamentos, TipoLancamento.ENTRADA);
        BigDecimal totalSaidas = somar(lancamentos, TipoLancamento.SAIDA);

        List<CategoriaTotalResponse> porCategoria = lancamentos.stream()
                .collect(Collectors.groupingBy(l -> l.getCategoria().getId()))
                .values().stream()
                .map(grupo -> new CategoriaTotalResponse(
                        grupo.get(0).getCategoria().getId(),
                        grupo.get(0).getCategoria().getNome(),
                        grupo.get(0).getTipo(),
                        grupo.stream().map(Lancamento::getValor).reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .sorted(Comparator.comparing(CategoriaTotalResponse::categoriaNome))
                .toList();

        List<LancamentoResponse> itens = lancamentos.stream().map(LancamentoResponse::from).toList();

        return new RelatorioResponse(
                dataInicio, dataFim,
                totalEntradas, totalSaidas, totalEntradas.subtract(totalSaidas),
                porCategoria, itens
        );
    }

    private BigDecimal somar(List<Lancamento> lancamentos, TipoLancamento tipo) {
        return lancamentos.stream()
                .filter(l -> l.getTipo() == tipo)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
