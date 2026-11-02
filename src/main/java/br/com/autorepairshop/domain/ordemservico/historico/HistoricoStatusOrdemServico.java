package br.com.autorepairshop.domain.ordemservico.historico;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;

public class HistoricoStatusOrdemServico {

    private final UUID id;
    private final UUID ordemServicoId;
    private final StatusOrdemServico statusAnterior;
    private final StatusOrdemServico statusNovo;
    private final LocalDateTime dataHora;

    private HistoricoStatusOrdemServico(
            UUID id,
            UUID ordemServicoId,
            StatusOrdemServico statusAnterior,
            StatusOrdemServico statusNovo,
            LocalDateTime dataHora) {

        this.id = id;
        this.ordemServicoId = ordemServicoId;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.dataHora = dataHora;
    }

    public static HistoricoStatusOrdemServico registrar(
            UUID ordemServicoId,
            StatusOrdemServico statusAnterior,
            StatusOrdemServico statusNovo) {

        return new HistoricoStatusOrdemServico(
                UUID.randomUUID(),
                ordemServicoId,
                statusAnterior,
                statusNovo,
                LocalDateTime.now());
    }

    public static HistoricoStatusOrdemServico reconstituir(
            UUID id,
            UUID ordemServicoId,
            StatusOrdemServico statusAnterior,
            StatusOrdemServico statusNovo,
            LocalDateTime dataHora) {

        return new HistoricoStatusOrdemServico(
                id,
                ordemServicoId,
                statusAnterior,
                statusNovo,
                dataHora);
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrdemServicoId() {
        return ordemServicoId;
    }

    public StatusOrdemServico getStatusAnterior() {
        return statusAnterior;
    }

    public StatusOrdemServico getStatusNovo() {
        return statusNovo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}