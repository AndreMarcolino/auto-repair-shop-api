
package br.com.autorepairshop.application.ordemservico.calculartempomedioexecucao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;

@Service
public class CalcularTempoMedioExecucaoService  implements CalcularTempoMedioExecucaoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;

    public CalcularTempoMedioExecucaoService(OrdemServicoRepository ordemServicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CalcularTempoMedioExecucaoOutput executar() {

        List<OrdemServico> ordensServico =ordemServicoRepository.buscarPorStatus(StatusOrdemServico.ENTREGUE);

        List<CalcularTempoMedioExecucaoOutput.OrdemServicoTempoOutput> resultados = new ArrayList<>();

        long totalSegundosAtendimento = 0;
        long quantidadeComTempoAtendimento = 0;

        long totalSegundosExecucao = 0;
        long quantidadeComTempoExecucao = 0;

        for (OrdemServico ordemServico : ordensServico) {

            Duration tempoAtendimento = ordemServico
                    .calcularTempoTotalAtendimento()
                    .orElse(null);

            Duration tempoExecucao = ordemServico
                    .calcularTempoExecucao()
                    .orElse(null);

            BigDecimal atendimentoMinutos = null;
            BigDecimal atendimentoDias = null;

            BigDecimal execucaoMinutos = null;
            BigDecimal execucaoDias = null;

            if (tempoAtendimento != null) {
                totalSegundosAtendimento += tempoAtendimento.getSeconds();
                quantidadeComTempoAtendimento++;

                atendimentoMinutos = paraMinutos(tempoAtendimento);
                atendimentoDias = paraDias(tempoAtendimento);
            }

            if (tempoExecucao != null) {
                totalSegundosExecucao += tempoExecucao.getSeconds();
                quantidadeComTempoExecucao++;

                execucaoMinutos = paraMinutos(tempoExecucao);
                execucaoDias = paraDias(tempoExecucao);
            }

            resultados.add(
                new CalcularTempoMedioExecucaoOutput.OrdemServicoTempoOutput(
                    ordemServico.getNumero(),
                    atendimentoMinutos,
                    atendimentoDias,
                    execucaoMinutos,
                    execucaoDias
                )
            );
        }

        BigDecimal tempoMedioAtendimentoMinutos =
                calcularMediaEmMinutos(
                        totalSegundosAtendimento,
                        quantidadeComTempoAtendimento);

        BigDecimal tempoMedioAtendimentoDias =
                calcularMediaEmDias(
                        totalSegundosAtendimento,
                        quantidadeComTempoAtendimento);

        BigDecimal tempoMedioExecucaoMinutos =
                calcularMediaEmMinutos(
                        totalSegundosExecucao,
                        quantidadeComTempoExecucao);

        BigDecimal tempoMedioExecucaoDias =
                calcularMediaEmDias(
                        totalSegundosExecucao,
                        quantidadeComTempoExecucao);

        return new CalcularTempoMedioExecucaoOutput(
                ordensServico.size(),
                quantidadeComTempoAtendimento,
                tempoMedioAtendimentoMinutos,
                tempoMedioAtendimentoDias,
                quantidadeComTempoExecucao,
                tempoMedioExecucaoMinutos,
                tempoMedioExecucaoDias,
                List.copyOf(resultados)
        );
    }

    private BigDecimal calcularMediaEmMinutos(
            long totalSegundos,
            long quantidade) {

        if (quantidade == 0) {
            return BigDecimal.ZERO.setScale(2);
        }

        return BigDecimal.valueOf(totalSegundos)
                .divide(
                    BigDecimal.valueOf(quantidade * 60L),
                    2,
                    RoundingMode.HALF_UP
                );
    }

    private BigDecimal calcularMediaEmDias(
            long totalSegundos,
            long quantidade) {

        if (quantidade == 0) {
            return BigDecimal.ZERO.setScale(2);
        }

        return BigDecimal.valueOf(totalSegundos)
                .divide(
                    BigDecimal.valueOf(quantidade * 86400L),
                    2,
                    RoundingMode.HALF_UP
                );
    }

    private BigDecimal paraMinutos(Duration duracao) {
        return BigDecimal.valueOf(duracao.getSeconds())
                .divide(
                    BigDecimal.valueOf(60),
                    2,
                    RoundingMode.HALF_UP
                );
    }

    private BigDecimal paraDias(Duration duracao) {
        return BigDecimal.valueOf(duracao.getSeconds())
                .divide(
                    BigDecimal.valueOf(86400),
                    2,
                    RoundingMode.HALF_UP
                );
    }
}