package com.brunofinanceiro.financeiro.lancamento;

import com.brunofinanceiro.financeiro.categoria.TipoLancamento;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class LancamentoSpecifications {

    private LancamentoSpecifications() {
    }

    public static Specification<Lancamento> comFiltros(LocalDate dataInicio, LocalDate dataFim,
                                                         TipoLancamento tipo, Long categoriaId) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (dataInicio != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("dataLancamento"), dataInicio));
            }
            if (dataFim != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("dataLancamento"), dataFim));
            }
            if (tipo != null) {
                predicates = cb.and(predicates, cb.equal(root.get("tipo"), tipo));
            }
            if (categoriaId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("categoria").get("id"), categoriaId));
            }

            query.orderBy(cb.desc(root.get("dataLancamento")), cb.desc(root.get("id")));
            return predicates;
        };
    }
}
