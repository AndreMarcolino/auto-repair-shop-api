package br.com.autorepairshop.application.veiculo.criar;

public record CriarVeiculoInput(
	    String placa,
	    String marca,
	    String modelo,
	    Integer anoFabricacao
	) {
	}