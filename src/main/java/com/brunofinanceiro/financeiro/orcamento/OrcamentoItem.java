package com.brunofinanceiro.financeiro.orcamento;

import com.brunofinanceiro.financeiro.item.Item;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "orcamento_itens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrcamentoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcamento_id")
    private Orcamento orcamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "descricao_personalizada", length = 255)
    private String descricaoPersonalizada;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorUnitario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_desconto", nullable = false, length = 12)
    private TipoDesconto tipoDesconto;

    @Column(name = "valor_desconto", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorDesconto;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    public void recalcularValorTotal() {
        BigDecimal bruto = valorUnitario.multiply(quantidade);
        BigDecimal desconto = switch (tipoDesconto) {
            case NENHUM -> BigDecimal.ZERO;
            case VALOR_FIXO -> valorDesconto == null ? BigDecimal.ZERO : valorDesconto;
            case PERCENTUAL -> valorDesconto == null
                    ? BigDecimal.ZERO
                    : bruto.multiply(valorDesconto).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        };
        if (desconto.compareTo(bruto) > 0) {
            desconto = bruto;
        }
        this.valorTotal = bruto.subtract(desconto).setScale(2, RoundingMode.HALF_UP);
    }
}
