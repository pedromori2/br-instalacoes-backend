package com.brunofinanceiro.financeiro.categoria;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaLancamentoRepository extends JpaRepository<CategoriaLancamento, Long> {

    List<CategoriaLancamento> findByTipo(TipoLancamento tipo);
}
