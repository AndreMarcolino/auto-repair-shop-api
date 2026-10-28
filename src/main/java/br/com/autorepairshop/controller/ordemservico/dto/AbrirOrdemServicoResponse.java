package br.com.autorepairshop.controller.ordemservico.dto;

import br.com.autorepairshop.application.ordemservico.abrirordemservico.AbrirOrdemServicoOutput;

public record AbrirOrdemServicoResponse(
        String numero,
        String codigoAcesso
) {
    public static AbrirOrdemServicoResponse toResponse(
            AbrirOrdemServicoOutput output) {

        return new AbrirOrdemServicoResponse(
                output.numero(),
                output.codigoAcesso()
        );
    }
}
