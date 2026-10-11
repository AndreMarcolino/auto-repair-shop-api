package br.com.autorepairshop.controller.peca.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.peca.criar.CriarPecaInput;

public record CriarPecaRequest(
        String descricao,
        BigDecimal valorReferencia,
        Integer quantidadeEstoque
) {
    public CriarPecaInput paraInput() {
        return new CriarPecaInput(descricao, valorReferencia, quantidadeEstoque);
    }
}
