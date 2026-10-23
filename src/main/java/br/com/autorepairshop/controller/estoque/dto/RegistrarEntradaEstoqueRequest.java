package br.com.autorepairshop.controller.estoque.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.estoque.registrarentrada.RegistrarEntradaEstoqueInput;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public record RegistrarEntradaEstoqueRequest(
        @NotNull
        @Schema(description = "ID do insumo (a vela de ignição está com estoque zerado)", example = "44444444-4444-4444-4444-000000000005")
        String insumoId,

        @NotNull
        @DecimalMin(value = "0.0001")
        @Schema(description = "Quantidade de entrada", example = "10")
        BigDecimal quantidade
) {
    public RegistrarEntradaEstoqueInput paraInput() {
        return new RegistrarEntradaEstoqueInput(
                insumoId,
                quantidade
        );
    }
}
