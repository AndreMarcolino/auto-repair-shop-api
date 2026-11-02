package br.com.autorepairshop.application.servico.criar;

import java.math.BigDecimal;

public record CriarServicoInput(
        String descricao,
        BigDecimal valorReferencia
) {}
