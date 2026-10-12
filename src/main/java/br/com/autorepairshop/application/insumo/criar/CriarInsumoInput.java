package br.com.autorepairshop.application.insumo.criar;

import java.math.BigDecimal;

public record CriarInsumoInput(
        String descricao,
        BigDecimal valorReferencia,
        Integer quantidadeEstoque
) {}
