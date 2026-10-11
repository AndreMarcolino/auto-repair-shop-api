package br.com.autorepairshop.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.domain.peca.Peca;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pecas")
public class PecaEntity {

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

    protected PecaEntity() {
    }

    public static PecaEntity fromDomain(Peca peca) {
        PecaEntity entity = new PecaEntity();
        entity.id = peca.getId();
        entity.descricao = peca.getDescricao();
        entity.valorReferencia = peca.getValorReferencia();
        entity.quantidadeEstoque = peca.getQuantidadeEstoque();
        entity.criadoEm = peca.getCriadoEm();
        entity.atualizadoEm = peca.getAtualizadoEm();
        return entity;
    }

    public Peca toDomain() {
        return Peca.reconstituir(
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
