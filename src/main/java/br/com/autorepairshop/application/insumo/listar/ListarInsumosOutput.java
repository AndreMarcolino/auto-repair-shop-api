package br.com.autorepairshop.application.insumo.listar;

import java.util.List;

import br.com.autorepairshop.application.insumo.shared.InsumoOutput;
import br.com.autorepairshop.application.shared.dto.PaginacaoOutput;

public record ListarInsumosOutput(
        List<InsumoOutput> insumos,
        PaginacaoOutput paginacao
) {
}
