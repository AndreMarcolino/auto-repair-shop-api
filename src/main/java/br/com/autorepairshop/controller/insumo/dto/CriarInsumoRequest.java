package br.com.autorepairshop.controller.insumo.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.insumo.criar.CriarInsumoInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record CriarInsumoRequest(
        @Schema(description = "Descrição do insumo", example = "Fluido de freio DOT4 (500ml)")
        String descricao,
        @Schema(description = "Valor de referência do insumo", example = "39.90")
        BigDecimal valorReferencia
) {
    public CriarInsumoInput toInput() {
        return new CriarInsumoInput(descricao, valorReferencia);
    }
}
