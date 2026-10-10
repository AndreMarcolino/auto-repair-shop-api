package br.com.autorepairshop.application.veiculo.update;

public record AlterarVeiculoInput(
    String id,
    String placa,
    String marca,
    String modelo,
    Integer anoFabricacao
) {
}
