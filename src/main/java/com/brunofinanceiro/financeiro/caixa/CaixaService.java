package com.brunofinanceiro.financeiro.caixa;

import com.brunofinanceiro.financeiro.caixa.dto.FecharCaixaRequest;
import com.brunofinanceiro.financeiro.categoria.TipoLancamento;
import com.brunofinanceiro.financeiro.common.exception.BusinessException;
import com.brunofinanceiro.financeiro.common.exception.ResourceNotFoundException;
import com.brunofinanceiro.financeiro.lancamento.Lancamento;
import com.brunofinanceiro.financeiro.lancamento.LancamentoRepository;
import com.brunofinanceiro.financeiro.usuario.Usuario;
import com.brunofinanceiro.financeiro.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CaixaService {

    private final FechamentoCaixaRepository fechamentoRepository;
    private final LancamentoRepository lancamentoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<FechamentoCaixa> historico() {
        return fechamentoRepository.findAllByOrderByDataFechamentoDesc();
    }

    @Transactional(readOnly = true)
    public FechamentoCaixa resumoDoDia(LocalDate data) {
        return fechamentoRepository.findByDataFechamento(data)
                .orElseGet(() -> montarResumoNaoPersistido(data));
    }

    @Transactional
    public FechamentoCaixa fechar(LocalDate data, FecharCaixaRequest request, Long usuarioId) {
        if (fechamentoRepository.findByDataFechamento(data).isPresent()) {
            throw new BusinessException("O caixa do dia " + data + " ja foi fechado");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", usuarioId));

        FechamentoCaixa resumo = montarResumoNaoPersistido(data);
        resumo.setSaldoFinalInformado(request.saldoFinalInformado());
        resumo.setDiferenca(request.saldoFinalInformado().subtract(resumo.getSaldoFinalCalculado()));
        resumo.setObservacoes(request.observacoes());
        resumo.setStatus(StatusFechamento.FECHADO);
        resumo.setUsuario(usuario);
        resumo.setDataHoraFechamento(Instant.now());

        FechamentoCaixa salvo = fechamentoRepository.save(resumo);

        List<Lancamento> lancamentosDoDia = lancamentoRepository.findByDataLancamento(data);
        for (Lancamento lancamento : lancamentosDoDia) {
            lancamento.setFechamentoCaixa(salvo);
        }
        lancamentoRepository.saveAll(lancamentosDoDia);

        return salvo;
    }

    @Transactional
    public void reabrir(Long fechamentoId) {
        FechamentoCaixa fechamento = fechamentoRepository.findById(fechamentoId)
                .orElseThrow(() -> ResourceNotFoundException.of("Fechamento de caixa", fechamentoId));

        List<Lancamento> lancamentosVinculados = lancamentoRepository.findByFechamentoCaixaId(fechamentoId);
        for (Lancamento lancamento : lancamentosVinculados) {
            lancamento.setFechamentoCaixa(null);
        }
        lancamentoRepository.saveAll(lancamentosVinculados);

        fechamentoRepository.delete(fechamento);
    }

    private FechamentoCaixa montarResumoNaoPersistido(LocalDate data) {
        BigDecimal saldoInicial = fechamentoRepository
                .findTopByStatusAndDataFechamentoLessThanOrderByDataFechamentoDesc(StatusFechamento.FECHADO, data)
                .map(FechamentoCaixa::getSaldoFinalCalculado)
                .orElse(BigDecimal.ZERO);

        List<Lancamento> lancamentosDoDia = lancamentoRepository.findByDataLancamento(data);

        BigDecimal totalEntradas = lancamentosDoDia.stream()
                .filter(l -> l.getTipo() == TipoLancamento.ENTRADA)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaidas = lancamentosDoDia.stream()
                .filter(l -> l.getTipo() == TipoLancamento.SAIDA)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoFinalCalculado = saldoInicial.add(totalEntradas).subtract(totalSaidas);

        return FechamentoCaixa.builder()
                .dataFechamento(data)
                .saldoInicial(saldoInicial)
                .totalEntradas(totalEntradas)
                .totalSaidas(totalSaidas)
                .saldoFinalCalculado(saldoFinalCalculado)
                .status(StatusFechamento.ABERTO)
                .build();
    }
}
