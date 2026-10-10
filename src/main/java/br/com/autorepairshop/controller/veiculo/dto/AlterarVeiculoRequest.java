package br.com.autorepairshop.controller.veiculo.dto;

import br.com.autorepairshop.application.veiculo.update.AlterarVeiculoInput;

public record AlterarVeiculoRequest(
    String placa,
    String marca,
    String modelo,
    Integer anoFabricacao
) {

    public AlterarVeiculoInput paraInput(String id) {
        return new AlterarVeiculoInput(
            id,
            placa,
            marca,
            modelo,
            anoFabricacao
        );
    }
}