package br.com.autorepairshop.application.veiculo.listar;

import java.util.List;

import br.com.autorepairshop.application.shared.dto.PaginacaoOutput;
import br.com.autorepairshop.application.veiculo.shared.VeiculoOutput;

public record ListarVeiculosOutput(
    List<VeiculoOutput> veiculos,
    PaginacaoOutput paginacao
) {
}