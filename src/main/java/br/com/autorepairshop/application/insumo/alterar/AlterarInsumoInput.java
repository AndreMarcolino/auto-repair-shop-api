package br.com.autorepairshop.application.insumo.alterar;

import java.math.BigDecimal;

public record AlterarInsumoInput(
        String id,
        String descricao,
        BigDecimal valorReferencia,
        Integer quantidadeEstoque) {
}
