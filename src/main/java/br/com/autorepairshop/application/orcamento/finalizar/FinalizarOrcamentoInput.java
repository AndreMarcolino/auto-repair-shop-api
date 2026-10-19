package br.com.autorepairshop.application.orcamento.finalizar;

public record FinalizarOrcamentoInput(
        String ordemServicoId,
        String diagnostico
) {
}