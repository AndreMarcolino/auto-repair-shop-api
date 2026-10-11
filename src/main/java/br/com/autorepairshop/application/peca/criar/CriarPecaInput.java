package br.com.autorepairshop.application.peca.criar;

import java.math.BigDecimal;

public record CriarPecaInput(
        String descricao,
        BigDecimal valorReferencia,
        Integer quantidadeEstoque
) {}
