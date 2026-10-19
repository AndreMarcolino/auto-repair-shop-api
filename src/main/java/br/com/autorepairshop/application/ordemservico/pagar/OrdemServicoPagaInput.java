package br.com.autorepairshop.application.ordemservico.pagar;

public record OrdemServicoPagaInput(
        String idOrdemServico,
        String atendenteId
) {
}
