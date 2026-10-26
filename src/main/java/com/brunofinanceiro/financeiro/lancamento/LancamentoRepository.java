package com.brunofinanceiro.financeiro.lancamento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, JpaSpecificationExecutor<Lancamento> {

    List<Lancamento> findByDataLancamento(LocalDate dataLancamento);

    List<Lancamento> findByFechamentoCaixaId(Long fechamentoCaixaId);

    boolean existsByOrcamentoId(Long orcamentoId);
}
