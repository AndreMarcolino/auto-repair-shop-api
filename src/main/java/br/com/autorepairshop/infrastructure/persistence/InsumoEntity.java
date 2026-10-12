package br.com.autorepairshop.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.domain.insumo.Insumo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "insumos")
public class InsumoEntity {

    @Id
    private UUID id;

    @Column(name = "descricao", nullable = false, length = 200)
    private String descricao;

    @Column(name = "valor_referencia", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorReferencia;

    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected InsumoEntity() {
    }

    public static InsumoEntity fromDomain(Insumo insumo) {
        InsumoEntity entity = new InsumoEntity();
        entity.id = insumo.getId();
        entity.descricao = insumo.getDescricao();
        entity.valorReferencia = insumo.getValorReferencia();
        entity.quantidadeEstoque = insumo.getQuantidadeEstoque();
        entity.criadoEm = insumo.getCriadoEm();
        entity.atualizadoEm = insumo.getAtualizadoEm();
        return entity;
    }

    public Insumo toDomain() {
        return Insumo.reconstituir(
                id,
                descricao,
                valorReferencia,
                quantidadeEstoque,
                criadoEm,
                atualizadoEm);
    }

    public UUID getId() { return id; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValorReferencia() { return valorReferencia; }
    public Integer getQuantidadeEstoque() { return quantidadeEstoque; }
    public Instant getCriadoEm() { return criadoEm; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
}
