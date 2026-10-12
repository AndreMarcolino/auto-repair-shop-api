package br.com.autorepairshop.application.servico.listar;

import java.util.List;

import br.com.autorepairshop.application.servico.shared.ServicoOutput;
import br.com.autorepairshop.application.shared.dto.PaginacaoOutput;

public record ListarServicosOutput(
    List<ServicoOutput> servicos,
    PaginacaoOutput paginacao
) {
}