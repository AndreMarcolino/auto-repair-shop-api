
package br.com.autorepairshop.controller.veiculo.dto;

import br.com.autorepairshop.application.veiculo.listar.ListarVeiculosInput;

public record ListarVeiculosRequest(
        String placa,
        String marca,
        String modelo,
        Integer anoFabricacao,
        int pagina,
        int tamanho
) {
    public ListarVeiculosInput toInput() {
        return new ListarVeiculosInput(
                placa,
                marca,
                modelo,
                anoFabricacao
        );
    }
}

