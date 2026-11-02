package br.com.autorepairshop.controller.veiculo.dto;

import br.com.autorepairshop.application.veiculo.criar.CriarVeiculoInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public record CriarVeiculoRequest(
	    @NotBlank
	    @Schema(description = "Placa no padrão antigo (ABC1234) ou Mercosul (ABC1D23)", example = "MNO4P56")
	    String placa,

	    @NotBlank
	    @Schema(description = "Marca do veículo", example = "Honda")
	    String marca,

	    @NotBlank
	    @Schema(description = "Modelo do veículo", example = "Civic")
	    String modelo,

	    @NotNull
	    @Schema(description = "Ano de fabricação", example = "2023")
	    Integer anoFabricacao
	) {
	    public CriarVeiculoInput toInput() {
	        return new CriarVeiculoInput(
	            placa,
	            marca,
	            modelo,
	            anoFabricacao
	        );
	    }
	}
