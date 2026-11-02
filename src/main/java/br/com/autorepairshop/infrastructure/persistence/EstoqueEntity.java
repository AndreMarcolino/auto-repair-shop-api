package br.com.autorepairshop.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import br.com.autorepairshop.domain.estoque.Estoque;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estoque")
public class EstoqueEntity {

    @Id
    private UUID id;

    @Column(name = "insumo_id", nullable = false, unique = true)
    private UUID insumoId;

    @Column(
        name = "quantidade_disponivel",
        nullable = false,
        precision = 12,
        scale = 3
    )
    private BigDecimal quantidadeDisponivel;

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    protected EstoqueEntity() {
    }

    public UUID getId() {
        return id;
    }

    public UUID getInsumoId() {
        return insumoId;
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
    
    public static EstoqueEntity fromDomain(Estoque estoque) {
        EstoqueEntity entity = new EstoqueEntity();
        entity.id = estoque.getId();
        entity.insumoId = estoque.getInsumoId();
        entity.quantidadeDisponivel = estoque.getQuantidadeDisponivel();
        entity.atualizadoEm = OffsetDateTime.now();
        return entity;
    }  
    
    public Estoque toDomain() {
    	return Estoque.recriar(
                id,
                insumoId,
                quantidadeDisponivel
        );
    }
}