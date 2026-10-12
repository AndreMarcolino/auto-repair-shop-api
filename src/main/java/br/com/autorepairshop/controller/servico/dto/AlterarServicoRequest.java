package br.com.autorepairshop.controller.servico.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.servico.alterar.AlterarServicoInput;

public record AlterarServicoRequest(
	String descricao,
    BigDecimal valorReferencia
) {

    public AlterarServicoInput toInput(String id) {
        return new AlterarServicoInput(
            id,
            descricao,
            valorReferencia
        );
    }
}