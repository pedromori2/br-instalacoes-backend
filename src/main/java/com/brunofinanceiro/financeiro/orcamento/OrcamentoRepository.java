package com.brunofinanceiro.financeiro.orcamento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    List<Orcamento> findByStatus(StatusOrcamento status);

    List<Orcamento> findByClienteId(Long clienteId);
}
