package br.com.autorepairshop.application.estoque.registrarentrada;

import java.math.BigDecimal;

public record RegistrarEntradaEstoqueInput(
        String insumoId,
        BigDecimal quantidade
) {
}