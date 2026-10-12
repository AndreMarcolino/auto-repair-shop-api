package br.com.autorepairshop.controller.veiculo.dto;

import br.com.autorepairshop.application.veiculo.alterar.AlterarVeiculoInput;

public record AlterarVeiculoRequest(
    String placa,
    String marca,
    String modelo,
    Integer anoFabricacao
) {

    public AlterarVeiculoInput toInput(String id) {
        return new AlterarVeiculoInput(
            id,
            placa,
            marca,
            modelo,
            anoFabricacao
        );
    }
}