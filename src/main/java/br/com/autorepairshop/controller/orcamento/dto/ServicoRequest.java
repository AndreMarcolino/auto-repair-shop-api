package br.com.autorepairshop.controller.orcamento.dto;

import java.math.BigDecimal;

public record ServicoRequest(
        String idServico,
        BigDecimal quantidade
) {
}