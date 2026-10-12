package br.com.autorepairshop.controller.insumo.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.insumo.alterar.AlterarInsumoInput;

public record AlterarInsumoRequest(
        String descricao,
        BigDecimal valorReferencia,
        Integer quantidadeEstoque) {

    public AlterarInsumoInput toInput(String id) {
        return new AlterarInsumoInput(
                id,
                descricao,
                valorReferencia,
                quantidadeEstoque
        );
    }
}