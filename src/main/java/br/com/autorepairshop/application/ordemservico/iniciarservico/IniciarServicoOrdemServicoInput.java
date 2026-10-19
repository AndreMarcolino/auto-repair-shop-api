package br.com.autorepairshop.application.ordemservico.iniciarservico;

public record IniciarServicoOrdemServicoInput(
        String idOrdemServico,
        String mecanicoId
) {
}