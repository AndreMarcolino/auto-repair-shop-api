package br.com.autorepairshop.application.servico.listar;

import br.com.autorepairshop.application.shared.dto.PaginacaoInput;

public interface ListarServicosUseCase {

    ListarServicosOutput executar(ListarServicosInput input, PaginacaoInput paginacaoInput);
}