package br.com.autorepairshop.application.ordemservico.efetuarentrega;

public record EfetuarEntregaInput(
        String idOrdemServico,
        String atendenteId
) {
}