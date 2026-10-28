package br.com.autorepairshop.application.cliente.listar;

import br.com.autorepairshop.application.shared.dto.PaginacaoInput;

public interface ListarClientesUseCase {

	 ListarClientesOutput executar(ListarClientesInput input, PaginacaoInput paginacao);
}
