package br.com.autorepairshop.controller.orcamento.dto;

import java.math.BigDecimal;

public record InsumoRequest(
        String idInsumo,
        BigDecimal quantidade
) {
}
