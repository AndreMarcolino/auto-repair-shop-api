package br.com.autorepairshop.application.ordemservico.finalizarservico;

public record FinalizarServicoOrdemServicoInput(
        String idOrdemServico,
        String mecanicoId
) {
}