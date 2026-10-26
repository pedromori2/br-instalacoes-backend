package com.brunofinanceiro.financeiro.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cobre o fechamento diario de caixa: soma dos lancamentos do dia, calculo da
 * diferenca em relacao ao valor contado, e o travamento dos lancamentos apos o fechamento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CaixaFechamentoIntegrationTest extends IntegrationTestSupport {

    @LocalServerPort
    private int port;

    @Test
    void aoFecharCaixa_deveCalcularSaldoEDiferencaETravarLancamentosDoDia() {
        String baseUrl = "http://localhost:" + port;
        TestRestTemplate client = new TestRestTemplate().withBaseUrl(baseUrl);
        String token = autenticar(client);

        LocalDate hoje = LocalDate.now().plusDays(100); // dia isolado, sem fechamentos anteriores no teste

        Long categoriaEntradaId = buscarCategoriaId(client, token, "ENTRADA", "Venda de Produto");
        Long categoriaSaidaId = buscarCategoriaId(client, token, "SAIDA", "Despesas Fixas");

        criarLancamento(client, token, "ENTRADA", categoriaEntradaId, "100.00", hoje);
        criarLancamento(client, token, "SAIDA", categoriaSaidaId, "30.00", hoje);

        ResponseEntity<Map> resumoResp = client.exchange(
                "/api/caixa/fechamentos/" + hoje, HttpMethod.GET, autenticado(null, token), Map.class);
        assertThat(new BigDecimal(resumoResp.getBody().get("saldoFinalCalculado").toString()))
                .isEqualByComparingTo("70.00");

        ResponseEntity<Map> fecharResp = exchange(client, "/api/caixa/fechamentos/" + hoje + "/fechar", HttpMethod.POST,
                Map.of("saldoFinalInformado", new BigDecimal("70.00")), token, Map.class);
        assertThat(fecharResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(new BigDecimal(fecharResp.getBody().get("diferenca").toString())).isEqualByComparingTo("0.00");
        assertThat(fecharResp.getBody().get("status")).isEqualTo("FECHADO");

        // Fechar novamente o mesmo dia deve falhar
        ResponseEntity<Map> fecharDeNovoResp = exchange(client, "/api/caixa/fechamentos/" + hoje + "/fechar", HttpMethod.POST,
                Map.of("saldoFinalInformado", new BigDecimal("70.00")), token, Map.class);
        assertThat(fecharDeNovoResp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // Lancamentos do dia agora devem estar travados para edicao
        ResponseEntity<List> lancamentosResp = client.exchange(
                "/api/lancamentos?dataInicio=" + hoje + "&dataFim=" + hoje,
                HttpMethod.GET, autenticado(null, token), List.class);
        List<Map<String, Object>> lancamentosDoDia = lancamentosResp.getBody();
        assertThat(lancamentosDoDia).allSatisfy(l -> assertThat(l.get("travadoPorFechamento")).isEqualTo(true));

        Long primeiroLancamentoId = ((Number) lancamentosDoDia.get(0).get("id")).longValue();
        ResponseEntity<Map> tentativaEdicaoResp = exchange(client, "/api/lancamentos/" + primeiroLancamentoId, HttpMethod.PUT,
                Map.of(
                        "tipo", "ENTRADA",
                        "categoriaId", categoriaEntradaId,
                        "descricao", "Tentativa de edicao",
                        "valor", new BigDecimal("999.00"),
                        "dataLancamento", hoje.toString(),
                        "formaPagamento", "DINHEIRO"
                ), token, Map.class);
        assertThat(tentativaEdicaoResp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    private Long buscarCategoriaId(TestRestTemplate client, String token, String tipo, String nome) {
        ResponseEntity<List> resp = client.exchange(
                "/api/categorias?tipo=" + tipo, HttpMethod.GET, autenticado(null, token), List.class);
        return resp.getBody().stream()
                .map(o -> (Map<String, Object>) o)
                .filter(c -> nome.equals(c.get("nome")))
                .map(c -> ((Number) c.get("id")).longValue())
                .findFirst()
                .orElseThrow();
    }

    private void criarLancamento(TestRestTemplate client, String token, String tipo, Long categoriaId,
                                  String valor, LocalDate data) {
        Map<String, Object> body = Map.of(
                "tipo", tipo,
                "categoriaId", categoriaId,
                "descricao", "Lancamento de teste",
                "valor", new BigDecimal(valor),
                "dataLancamento", data.toString(),
                "formaPagamento", "DINHEIRO"
        );
        ResponseEntity<Map> resp = exchange(client, "/api/lancamentos", HttpMethod.POST, body, token, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
