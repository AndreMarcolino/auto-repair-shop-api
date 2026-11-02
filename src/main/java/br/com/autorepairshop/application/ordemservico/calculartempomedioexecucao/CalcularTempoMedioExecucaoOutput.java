package br.com.autorepairshop.application.ordemservico.calculartempomedioexecucao;

import java.math.BigDecimal;
import java.util.List;

public record CalcularTempoMedioExecucaoOutput(
    long quantidadeOrdensServico,
    long quantidadeComTempoAtendimento,
    BigDecimal tempoMedioAtendimentoMinutos,
    BigDecimal tempoMedioAtendimentoDias,
    long quantidadeComTempoExecucao,
    BigDecimal tempoMedioExecucaoMinutos,
    BigDecimal tempoMedioExecucaoDias,
    List<OrdemServicoTempoOutput> ordensServico
) {

    public record OrdemServicoTempoOutput(
        String numero,
        BigDecimal tempoAtendimentoMinutos,
        BigDecimal tempoAtendimentoDias,
        BigDecimal tempoExecucaoMinutos,
        BigDecimal tempoExecucaoDias
    ) {
    }
}