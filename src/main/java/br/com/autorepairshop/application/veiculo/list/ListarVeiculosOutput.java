package br.com.autorepairshop.application.veiculo.list;

import java.util.List;

import br.com.autorepairshop.application.veiculo.shared.VeiculoOutput;

public record ListarVeiculosOutput(
    List<VeiculoOutput> veiculos,
    int pagina,
    int tamanho,
    long totalElementos,
    int totalPaginas
) {
}