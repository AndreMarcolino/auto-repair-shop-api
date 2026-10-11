package br.com.autorepairshop.application.veiculo.list;

public record ListarVeiculosInput(
    String placa,
    String marca,
    String modelo,
    Integer anoFabricacao,
    int pagina,
    int tamanho
) {
}