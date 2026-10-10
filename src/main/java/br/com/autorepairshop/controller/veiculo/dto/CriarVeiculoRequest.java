package br.com.autorepairshop.controller.veiculo.dto;

import br.com.autorepairshop.domain.veiculo.create.CriarVeiculoInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarVeiculoRequest(
	    @NotBlank
	    String placa,

	    @NotBlank
	    String marca,

	    @NotBlank
	    String modelo,

	    @NotNull
	    Integer anoFabricacao
	) {
	    public CriarVeiculoInput paraInput() {
	        return new CriarVeiculoInput(
	            placa,
	            marca,
	            modelo,
	            anoFabricacao
	        );
	    }
	}
