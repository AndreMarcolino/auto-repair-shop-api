package br.com.autorepairshop.application.orcamento.shared;

import java.math.BigDecimal;

public record ServicoOrcamentoInput(
        String idServico,
        BigDecimal quantidade
) {
}