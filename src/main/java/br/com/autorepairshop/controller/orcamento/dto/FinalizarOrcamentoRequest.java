package br.com.autorepairshop.controller.orcamento.dto;

import br.com.autorepairshop.application.orcamento.finalizar.FinalizarOrcamentoInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record FinalizarOrcamentoRequest(
        @Schema(description = "Diagnóstico do mecânico", example = "Sensor de oxigênio com defeito; necessário substituir e limpar bicos.")
        String diagnostico
) {
    public FinalizarOrcamentoInput paraInput(String ordemServicoId) {
        return new FinalizarOrcamentoInput(
                ordemServicoId,
                diagnostico
        );
    }
}
