package br.com.autorepairshop.application.orcamento.shared;

import java.math.BigDecimal;

public record InsumoOrcamentoInput(String idInsumo,BigDecimal quantidade) {
}
