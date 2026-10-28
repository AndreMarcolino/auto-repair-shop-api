package br.com.autorepairshop.controller.ordemservico.dto;

import java.math.BigDecimal;
import java.util.List;

import br.com.autorepairshop.application.ordemservico.calculartempomedioexecucao.CalcularTempoMedioExecucaoOutput;

public record CalcularTempoMedioExecucaoResponse(
    long quantidadeOrdensServico,
    long quantidadeComTempoAtendimento,
    BigDecimal tempoMedioAtendimentoMinutos,
    BigDecimal tempoMedioAtendimentoDias,
    long quantidadeComTempoExecucao,
    BigDecimal tempoMedioExecucaoMinutos,
    BigDecimal tempoMedioExecucaoDias,
    List<OrdemServicoTempoResponse> ordensServico
) {

    public record OrdemServicoTempoResponse(
        String numero,
        BigDecimal tempoAtendimentoMinutos,
        BigDecimal tempoAtendimentoDias,
        BigDecimal tempoExecucaoMinutos,
        BigDecimal tempoExecucaoDias
    ) {
    }

    public static CalcularTempoMedioExecucaoResponse from(
            CalcularTempoMedioExecucaoOutput output) {

        List<OrdemServicoTempoResponse> ordens = output.ordensServico()
            .stream()
            .map(ordem -> new OrdemServicoTempoResponse(
                ordem.numero(),
                ordem.tempoAtendimentoMinutos(),
                ordem.tempoAtendimentoDias(),
                ordem.tempoExecucaoMinutos(),
                ordem.tempoExecucaoDias()
            ))
            .toList();

        return new CalcularTempoMedioExecucaoResponse(
            output.quantidadeOrdensServico(),
            output.quantidadeComTempoAtendimento(),
            output.tempoMedioAtendimentoMinutos(),
            output.tempoMedioAtendimentoDias(),
            output.quantidadeComTempoExecucao(),
            output.tempoMedioExecucaoMinutos(),
            output.tempoMedioExecucaoDias(),
            ordens
        );
    }
}