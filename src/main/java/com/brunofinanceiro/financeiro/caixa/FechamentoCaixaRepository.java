package com.brunofinanceiro.financeiro.caixa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FechamentoCaixaRepository extends JpaRepository<FechamentoCaixa, Long> {

    Optional<FechamentoCaixa> findByDataFechamento(LocalDate dataFechamento);

    Optional<FechamentoCaixa> findTopByStatusAndDataFechamentoLessThanOrderByDataFechamentoDesc(
            StatusFechamento status, LocalDate dataFechamento);

    List<FechamentoCaixa> findAllByOrderByDataFechamentoDesc();
}
