package br.com.autorepairshop.controller.orcamento.dto;

import br.com.autorepairshop.application.orcamento.finalizar.FinalizarOrcamentoInput;

public record FinalizarOrcamentoRequest(
        String diagnostico
) {
    public FinalizarOrcamentoInput paraInput(String ordemServicoId) {
        return new FinalizarOrcamentoInput(
                ordemServicoId,
                diagnostico
        );
    }
}
