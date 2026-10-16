package br.com.autorepairshop.controller.ordemservico.dto;

import br.com.autorepairshop.application.ordemservico.abrirordemservico.AbrirOrdemServicoInput;

public record AbrirOrdemServicoRequest(
    String clienteId,
    String veiculoId,
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