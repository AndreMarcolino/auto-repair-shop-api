package br.com.autorepairshop.controller.insumo.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.insumo.criar.CriarInsumoInput;

public record CriarInsumoRequest(
        String descricao,
        BigDecimal valorReferencia
) {
    public CriarInsumoInput toInput() {
        return new CriarInsumoInput(descricao, valorReferencia);
    }
}
