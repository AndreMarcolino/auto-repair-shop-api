package br.com.autorepairshop.domain.ordemservico;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

public class OrdemServicoInsumoPendente {

    private final UUID id;
    private final UUID ordemServicoId;
    private final UUID insumoId;
    private BigDecimal quantidadeNecessaria;

    private OrdemServicoInsumoPendente(
            UUID id,
            UUID ordemServicoId,
            UUID insumoId,
            BigDecimal quantidadeNecessaria) {
        this.id = id;
        this.ordemServicoId = ordemServicoId;
        this.insumoId = insumoId;
        this.quantidadeNecessaria = quantidadeNecessaria;

        validar();
    }

    public static OrdemServicoInsumoPendente criar(
            UUID ordemServicoId,
            UUID insumoId,
            BigDecimal quantidadeNecessaria) {

        return new OrdemServicoInsumoPendente(
            UUID.randomUUID(),
            ordemServicoId,
            insumoId,
            quantidadeNecessaria
        );
    }

    public static OrdemServicoInsumoPendente recriar(
            UUID id,
            UUID ordemServicoId,
            UUID insumoId,
            BigDecimal quantidadeNecessaria) {

        return new OrdemServicoInsumoPendente(
            id,
            ordemServicoId,
            insumoId,
            quantidadeNecessaria
        );
    }

    public void atualizarQuantidadeNecessaria(BigDecimal quantidade) {
        validarQuantidade(quantidade);
        this.quantidadeNecessaria = quantidade;
    }

    private void validar() {
        if (id == null || ordemServicoId == null || insumoId == null) {
            throw new DomainException(DomainError.ID_ORDEM_SERVICO_INVALIDO);
        }

        validarQuantidade(quantidadeNecessaria);
    }

    private void validarQuantidade(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(DomainError.QUANTIDADE_ESTOQUE_INVALIDA);
        }
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
}