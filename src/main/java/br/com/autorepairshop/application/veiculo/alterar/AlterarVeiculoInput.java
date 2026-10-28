package br.com.autorepairshop.application.veiculo.alterar;

public record AlterarVeiculoInput(
    String id,
    String placa,
    String marca,
    String modelo,
    Integer anoFabricacao
) {
}
