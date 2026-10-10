package br.com.autorepairshop.domain.veiculo.create;

public record CriarVeiculoInput(
	    String placa,
	    String marca,
	    String modelo,
	    Integer anoFabricacao
	) {
	}