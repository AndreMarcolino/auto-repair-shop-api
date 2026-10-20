package br.com.autorepairshop.controller.estoque.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.estoque.registrarentrada.RegistrarEntradaEstoqueInput;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record RegistrarEntradaEstoqueRequest(
        @NotNull
        String insumoId,

        @NotNull
        @DecimalMin(value = "0.0001")
        BigDecimal quantidade
) {
    public RegistrarEntradaEstoqueInput paraInput() {
        return new RegistrarEntradaEstoqueInput(
                insumoId,
                quantidade
        );
    }
}
