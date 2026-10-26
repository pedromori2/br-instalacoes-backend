package com.brunofinanceiro.financeiro.caixa;

import com.brunofinanceiro.financeiro.caixa.dto.FecharCaixaRequest;
import com.brunofinanceiro.financeiro.caixa.dto.FechamentoResponse;
import com.brunofinanceiro.financeiro.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/caixa")
@RequiredArgsConstructor
public class CaixaController {

    private final CaixaService caixaService;

    @GetMapping("/fechamentos")
    public List<FechamentoResponse> historico() {
        return caixaService.historico().stream().map(FechamentoResponse::from).toList();
    }

    @GetMapping("/fechamentos/{data}")
    public FechamentoResponse resumoDoDia(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return FechamentoResponse.from(caixaService.resumoDoDia(data));
    }

    @PostMapping("/fechamentos/{data}/fechar")
    @ResponseStatus(HttpStatus.CREATED)
    public FechamentoResponse fechar(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                                      @Valid @RequestBody FecharCaixaRequest request,
                                      @AuthenticationPrincipal UserPrincipal principal) {
        return FechamentoResponse.from(caixaService.fechar(data, request, principal.getId()));
    }

    @PostMapping("/fechamentos/{id}/reabrir")
    @PreAuthorize("hasRole('ADMIN')")
    public void reabrir(@PathVariable Long id) {
        caixaService.reabrir(id);
    }
}
