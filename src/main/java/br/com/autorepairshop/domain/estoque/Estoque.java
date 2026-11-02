package br.com.autorepairshop.domain.estoque;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

public class Estoque {

    private final UUID id;
    private final UUID insumoId;
    private BigDecimal quantidadeDisponivel;

    private Estoque(
            UUID id,
            UUID insumoId,
            BigDecimal quantidadeDisponivel) {

        this.id = id;
        this.insumoId = insumoId;
        this.quantidadeDisponivel = quantidadeDisponivel;

        validar();
    }

    public static Estoque criar(UUID insumoId) {
        return new Estoque(
                UUID.randomUUID(),
                insumoId,
                BigDecimal.ZERO
        );
    }

    public static Estoque recriar(
            UUID id,
            UUID insumoId,
            BigDecimal quantidadeDisponivel) {

        return new Estoque(id, insumoId, quantidadeDisponivel);
    }

    public boolean possuiDisponibilidade(BigDecimal quantidade) {
        validarQuantidade(quantidade);

        return quantidadeDisponivel.compareTo(quantidade) >= 0;
    }

    public void baixar(BigDecimal quantidade) {
        validarQuantidade(quantidade);

        if (!possuiDisponibilidade(quantidade)) {
            throw new DomainException(
                    DomainError.ESTOQUE_INSUFICIENTE
            );
        }

        quantidadeDisponivel =
                quantidadeDisponivel.subtract(quantidade);
    }

    public void adicionar(BigDecimal quantidade) {
        validarQuantidade(quantidade);

        quantidadeDisponivel =
                quantidadeDisponivel.add(quantidade);
    }

    private void validar() {
        if (id == null || insumoId == null) {
            throw new DomainException(
                    DomainError.ID_INSUMO_INVALIDO
            );
        }

        if (quantidadeDisponivel == null
                || quantidadeDisponivel.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException(
                    DomainError.QUANTIDADE_ESTOQUE_INVALIDA
            );
        }
    }

    private void validarQuantidade(BigDecimal quantidade) {
        if (quantidade == null
                || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(
                    DomainError.QUANTIDADE_ESTOQUE_INVALIDA
            );
        }
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
}