package br.com.autorepairshop.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "historico_status_ordem_servico")
public class HistoricoStatusOrdemServicoEntity {

    @Id
    private UUID id;

    @Column(name = "ordem_servico_id", nullable = false)
    private UUID ordemServicoId;

    @Column(name = "status_anterior_id")
    private Short statusAnteriorId;

    @Column(name = "status_novo_id", nullable = false)
    private Short statusNovoId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    protected HistoricoStatusOrdemServicoEntity() {
    }

    public static HistoricoStatusOrdemServicoEntity toEntity(
            HistoricoStatusOrdemServico historico) {

        HistoricoStatusOrdemServicoEntity entity =
                new HistoricoStatusOrdemServicoEntity();

        entity.id = historico.getId();
        entity.ordemServicoId = historico.getOrdemServicoId();
        entity.statusAnteriorId = historico.getStatusAnterior() == null
                ? null
                : historico.getStatusAnterior().getId();
        entity.statusNovoId = historico.getStatusNovo().getId();
        entity.dataHora = historico.getDataHora();

        return entity;
    }

    public HistoricoStatusOrdemServico toDomain() {
        return HistoricoStatusOrdemServico.reconstituir(
                id,
                ordemServicoId,
                statusAnteriorId == null
                        ? null
                        : StatusOrdemServico.porId(statusAnteriorId),
                StatusOrdemServico.porId(statusNovoId),
                dataHora);
    }
}