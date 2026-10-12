package br.com.autorepairshop.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.domain.servico.Servico;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "servicos")
public class ServicoEntity {

    @Id
    private UUID id;

    @Column(name = "descricao", nullable = false, length = 200)
    private String descricao;

    @Column(name = "valor_referencia", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorReferencia;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected ServicoEntity() {
    }

    public static ServicoEntity toEntity(Servico servico) {
        ServicoEntity entity = new ServicoEntity();
        entity.id = servico.getId();
        entity.descricao = servico.getDescricao();
        entity.valorReferencia = servico.getValorReferencia();
        entity.criadoEm = servico.getCriadoEm();
        entity.atualizadoEm = servico.getAtualizadoEm();
        return entity;
    }

    public Servico toDomain() {
        return Servico.reconstituir(
                id,
                descricao,
                valorReferencia,
                criadoEm,
                atualizadoEm);
    }

    public UUID getId() { return id; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValorReferencia() { return valorReferencia; }
    public Instant getCriadoEm() { return criadoEm; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
}
