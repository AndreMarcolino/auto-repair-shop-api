package br.com.autorepairshop.controller.ordemservico.dto;

import br.com.autorepairshop.application.ordemservico.abrirordemservico.AbrirOrdemServicoInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record AbrirOrdemServicoRequest(
    @Schema(description = "ID do cliente", example = "11111111-1111-1111-1111-000000000001")
    String clienteId,
    @Schema(description = "ID do veículo", example = "22222222-2222-2222-2222-000000000001")
    String veiculoId,
    @Schema(description = "Problema relatado pelo cliente", example = "Ar-condicionado não está gelando.")
    String problemaRelatado
) {

    public AbrirOrdemServicoInput toInput() {
        return new AbrirOrdemServicoInput(
            clienteId,
           veiculoId,
           problemaRelatado
        );
    }
}