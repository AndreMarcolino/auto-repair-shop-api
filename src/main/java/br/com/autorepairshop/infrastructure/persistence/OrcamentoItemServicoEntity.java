package br.com.autorepairshop.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.autorepairshop.domain.orcamento.OrcamentoItemInsumo;
import br.com.autorepairshop.domain.orcamento.OrcamentoItemServico;

@Entity
@Table(
    name = "orcamento_item_servico",
    indexes = @Index(
        name = "idx_orcamento_item_servico_orcamento_id",
        columnList = "orcamento_id"
    )
)
public class OrcamentoItemServicoEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private OrcamentoEntity orcamento;

    @Column(name = "servico_id", nullable = false)
    private UUID servicoId;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidade;

    @Column(name = "valor_unitario", nullable = false,
            precision = 12, scale = 2)
    private BigDecimal valorUnitario;

    protected OrcamentoItemServicoEntity() {
        // Exigido pelo JPA
    }

    public OrcamentoItemServicoEntity(
            UUID id,
            UUID servicoId,
            String descricao,
            BigDecimal quantidade,
            BigDecimal valorUnitario) {

        this.id = id;
        this.servicoId = servicoId;
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

    public UUID getServicoId() {
        return servicoId;
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
    
    public OrcamentoItemServico toDomain() {
        return OrcamentoItemServico.recria(this.id, this.servicoId,this.descricao, this.quantidade,this.valorUnitario);
               
    }
}