package br.com.autorepairshop.infrastructure.persistence;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.autorepairshop.domain.orcamento.OrcamentoItemInsumo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "orcamento_item_insumo",
    indexes = @Index(
        name = "idx_orcamento_item_insumo_orcamento_id",
        columnList = "orcamento_id"
    )
)
public class OrcamentoItemInsumoEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private OrcamentoEntity orcamento;

    @Column(name = "insumo_id", nullable = false)
    private UUID insumoId;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidade;

    @Column(name = "valor_unitario", nullable = false,
            precision = 12, scale = 2)
    private BigDecimal valorUnitario;

    protected OrcamentoItemInsumoEntity() {
        // Exigido pelo JPA
    }

    public OrcamentoItemInsumoEntity(
            UUID id,
            UUID insumoId,
            String descricao,
            BigDecimal quantidade,
            BigDecimal valorUnitario) {

        this.id = id;
        this.insumoId = insumoId;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    void definirOrcamento(OrcamentoEntity orcamento) {
        this.orcamento = orcamento;
    }

    public UUID getId() {
        return id;
    }

    public OrcamentoEntity getOrcamento() {
        return orcamento;
    }

    public UUID getInsumoId() {
        return insumoId;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }
    
    public OrcamentoItemInsumo toDomain() {
        return OrcamentoItemInsumo.recria(this.id, this.insumoId,this.descricao, this.quantidade,this.valorUnitario);
               
    }
}