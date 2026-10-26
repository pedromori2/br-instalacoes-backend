package com.brunofinanceiro.financeiro.caixa;

import com.brunofinanceiro.financeiro.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "fechamentos_caixa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FechamentoCaixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_fechamento", nullable = false, unique = true)
    private LocalDate dataFechamento;

    @Column(name = "saldo_inicial", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoInicial;

    @Column(name = "total_entradas", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalEntradas;

    @Column(name = "total_saidas", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalSaidas;

    @Column(name = "saldo_final_calculado", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoFinalCalculado;

    @Column(name = "saldo_final_informado", precision = 12, scale = 2)
    private BigDecimal saldoFinalInformado;

    @Column(precision = 12, scale = 2)
    private BigDecimal diferenca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatusFechamento status;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "data_hora_fechamento")
    private Instant dataHoraFechamento;
}
