package br.com.autorepairshop.application.veiculo.lista;

public record ListarVeiculosInput(
    String placa,
    String marca,
    String modelo,
    Integer anoFabricacao,
    int pagina,
    int tamanho
) {
}