package br.com.autorepairshop.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import br.com.autorepairshop.domain.ordemservico.OrdemServicoInsumoPendente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ordem_servico_insumo_pendente")
public class OrdemServicoInsumoPendenteEntity {

    @Id
    private UUID id;

    @Column(name = "ordem_servico_id", nullable = false)
    private UUID ordemServicoId;

    @Column(name = "insumo_id", nullable = false)
    private UUID insumoId;

    @Column(
        name = "quantidade_necessaria",
        nullable = false,
        precision = 12,
        scale = 3
    )
    private BigDecimal quantidadeNecessaria;

    @Column(
        name = "criado_em",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private OffsetDateTime criadoEm;

    protected OrdemServicoInsumoPendenteEntity() {
    }

    public static OrdemServicoInsumoPendenteEntity fromDomain(
            OrdemServicoInsumoPendente domain) {

        var entity = new OrdemServicoInsumoPendenteEntity();
        entity.id = domain.getId();
        entity.ordemServicoId = domain.getOrdemServicoId();
        entity.insumoId = domain.getInsumoId();
        entity.quantidadeNecessaria = domain.getQuantidadeNecessaria();

        return entity;
    }

    public OrdemServicoInsumoPendente toDomain() {
        return OrdemServicoInsumoPendente.recriar(
            id,
            ordemServicoId,
            insumoId,
            quantidadeNecessaria
        );
    }

    public void atualizarDados(OrdemServicoInsumoPendente domain) {
        this.quantidadeNecessaria = domain.getQuantidadeNecessaria();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrdemServicoId() {
        return ordemServicoId;
    }

    public UUID getInsumoId() {
        return insumoId;
    }

    public BigDecimal getQuantidadeNecessaria() {
        return quantidadeNecessaria;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }
}