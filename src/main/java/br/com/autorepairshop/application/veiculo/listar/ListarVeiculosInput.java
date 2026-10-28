package br.com.autorepairshop.application.veiculo.listar;

public record ListarVeiculosInput(
    String placa,
    String marca,
    String modelo,
    Integer anoFabricacao
) {
}