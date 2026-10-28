package br.com.autorepairshop.application.veiculo.listar;

import br.com.autorepairshop.application.shared.dto.PaginacaoInput;

public interface ListarVeiculosUseCase {

    ListarVeiculosOutput executar(ListarVeiculosInput input, PaginacaoInput paginacao);
}