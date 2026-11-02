
package br.com.autorepairshop.controller.veiculo.dto;

import br.com.autorepairshop.application.veiculo.listar.ListarVeiculosInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record ListarVeiculosRequest(
        @Schema(description = "Placa exata", example = "ABC1234")
        String placa,
        @Schema(description = "Filtro por parte da marca", example = "Volks")
        String marca,
        @Schema(description = "Filtro por parte do modelo", example = "Gol")
        String modelo,
        @Schema(description = "Ano de fabricação", example = "2018")
        Integer anoFabricacao,
        @Schema(description = "Número da página (começa em 0)", example = "0")
        int pagina,
        @Schema(description = "Quantidade de itens por página", example = "10")
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

