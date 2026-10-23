package br.com.autorepairshop.controller.insumo.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.insumo.alterar.AlterarInsumoInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record AlterarInsumoRequest(
        @Schema(description = "Descrição do insumo", example = "Óleo de motor 5W30 sintético (litro)")
        String descricao,
        @Schema(description = "Valor de referência do insumo", example = "49.90")
        BigDecimal valorReferencia,
        @Schema(description = "Quantidade em estoque", example = "80")
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