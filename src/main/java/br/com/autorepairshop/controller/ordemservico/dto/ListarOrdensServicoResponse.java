package br.com.autorepairshop.controller.ordemservico.dto;

import java.util.List;

import br.com.autorepairshop.application.ordemservico.listar.ListarOrdensServicoOutput;

public record ListarOrdensServicoResponse(
        List<OrdemServicoResponse> ordensServico
) {
    public static ListarOrdensServicoResponse from(
            List<ListarOrdensServicoOutput> output) {

        return new ListarOrdensServicoResponse(
                output.stream()
                        .map(OrdemServicoResponse::from)
                        .toList()
        );
    }
}
