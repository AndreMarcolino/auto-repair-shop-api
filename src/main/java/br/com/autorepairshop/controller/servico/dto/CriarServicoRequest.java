package br.com.autorepairshop.controller.servico.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.servico.criar.CriarServicoInput;

public record CriarServicoRequest(
        String descricao,
        BigDecimal valorReferencia
) {
    public CriarServicoInput paraInput() {
        return new CriarServicoInput(descricao, valorReferencia);
    }
}
