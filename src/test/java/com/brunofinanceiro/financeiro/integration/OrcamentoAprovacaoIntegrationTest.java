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
 * Cobre o fluxo central do sistema: orcamento com item com desconto -> aprovacao ->
 * finalizacao -> geracao automatica do lancamento de entrada correspondente.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrcamentoAprovacaoIntegrationTest extends IntegrationTestSupport {

    @LocalServerPort
    private int port;

    @Test
    void aoFinalizarOrcamento_deveGerarLancamentoDeEntradaComValorComDesconto() {
        String baseUrl = "http://localhost:" + port;
        TestRestTemplate client = new TestRestTemplate().withBaseUrl(baseUrl);
        String token = autenticar(client);

        Map<String, Object> clienteBody = Map.of(
                "nome", "Cliente Teste",
                "documento", "123.456.789-00"
        );
        ResponseEntity<Map> clienteResp = exchange(client, "/api/clientes", HttpMethod.POST, clienteBody, token, Map.class);
        assertThat(clienteResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long clienteId = ((Number) clienteResp.getBody().get("id")).longValue();

        Map<String, Object> itemBody = Map.of(
                "nome", "Servico de Instalacao",
                "tipo", "SERVICO",
                "precoUnitario", new BigDecimal("100.00"),
                "unidade", "un"
        );
        ResponseEntity<Map> itemResp = exchange(client, "/api/itens", HttpMethod.POST, itemBody, token, Map.class);
        assertThat(itemResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long itemId = ((Number) itemResp.getBody().get("id")).longValue();

        Map<String, Object> itemOrcamento = Map.of(
                "itemId", itemId,
                "quantidade", new BigDecimal("2"),
                "tipoDesconto", "PERCENTUAL",
                "valorDesconto", new BigDecimal("10")
        );
        Map<String, Object> orcamentoBody = Map.of(
                "clienteId", clienteId,
                "dataOrcamento", LocalDate.now().toString(),
                "itens", List.of(itemOrcamento)
        );
        ResponseEntity<Map> orcamentoResp = exchange(client, "/api/orcamentos", HttpMethod.POST, orcamentoBody, token, Map.class);
        assertThat(orcamentoResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long orcamentoId = ((Number) orcamentoResp.getBody().get("id")).longValue();

        // 2 x 100 = 200 bruto, desconto de 10% = 20 -> total esperado 180
        assertThat(new BigDecimal(orcamentoResp.getBody().get("valorTotal").toString()))
                .isEqualByComparingTo("180.00");

        exchange(client, "/api/orcamentos/" + orcamentoId + "/status", HttpMethod.PATCH,
                Map.of("status", "ENVIADO"), token, Map.class);

        ResponseEntity<Map> aprovadoResp = exchange(client, "/api/orcamentos/" + orcamentoId + "/status", HttpMethod.PATCH,
                Map.of("status", "APROVADO"), token, Map.class);
        assertThat(aprovadoResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(aprovadoResp.getBody().get("status")).isEqualTo("APROVADO");

        // Finalizar sem informar forma de pagamento deve falhar
        ResponseEntity<Map> finalizarSemFormaResp = exchange(client, "/api/orcamentos/" + orcamentoId + "/status", HttpMethod.PATCH,
                Map.of("status", "FINALIZADO"), token, Map.class);
        assertThat(finalizarSemFormaResp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ResponseEntity<Map> finalizadoResp = exchange(client, "/api/orcamentos/" + orcamentoId + "/status", HttpMethod.PATCH,
                Map.of("status", "FINALIZADO", "formaPagamento", "PIX"), token, Map.class);
        assertThat(finalizadoResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(finalizadoResp.getBody().get("status")).isEqualTo("FINALIZADO");

        ResponseEntity<List> lancamentosResp = client.exchange(
                "/api/lancamentos", HttpMethod.GET, autenticado(null, token), List.class);
        List<Map<String, Object>> lancamentos = lancamentosResp.getBody();

        boolean existeLancamentoDoOrcamento = lancamentos.stream()
                .anyMatch(l -> orcamentoId.equals(((Number) l.get("orcamentoId")).longValue())
                        && new BigDecimal(l.get("valor").toString()).compareTo(new BigDecimal("180.00")) == 0);
        assertThat(existeLancamentoDoOrcamento).isTrue();

        // Tentar finalizar novamente deve falhar (transicao invalida a partir de FINALIZADO)
        ResponseEntity<Map> finalizarDeNovoResp = exchange(client, "/api/orcamentos/" + orcamentoId + "/status", HttpMethod.PATCH,
                Map.of("status", "FINALIZADO", "formaPagamento", "PIX"), token, Map.class);
        assertThat(finalizarDeNovoResp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
