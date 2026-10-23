package br.com.autorepairshop.controller.veiculo.dto;

import br.com.autorepairshop.application.veiculo.alterar.AlterarVeiculoInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record AlterarVeiculoRequest(
    @Schema(description = "Placa no padrão antigo (ABC1234) ou Mercosul (ABC1D23)", example = "ABC1234")
    String placa,
    @Schema(description = "Marca do veículo", example = "Volkswagen")
    String marca,
    @Schema(description = "Modelo do veículo", example = "Gol G7")
    String modelo,
    @Schema(description = "Ano de fabricação", example = "2019")
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