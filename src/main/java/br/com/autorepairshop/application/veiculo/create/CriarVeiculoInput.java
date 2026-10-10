package br.com.autorepairshop.application.veiculo.create;

public record CriarVeiculoInput(
	    String placa,
	    String marca,
	    String modelo,
	    Integer anoFabricacao
	) {
	}