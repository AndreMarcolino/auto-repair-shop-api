package br.com.autorepairshop.application.insumo.listar;

import br.com.autorepairshop.application.shared.dto.PaginacaoInput;

public interface ListarInsumosUseCase {

    ListarInsumosOutput executar(
            ListarInsumosInput input,
            PaginacaoInput paginacaoInput);
}