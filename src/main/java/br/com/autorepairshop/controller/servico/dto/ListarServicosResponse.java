
package br.com.autorepairshop.controller.servico.dto;

import java.util.List;

import br.com.autorepairshop.application.servico.listar.ListarServicosOutput;
import br.com.autorepairshop.controller.shared.dto.PaginacaoResponse;

public record ListarServicosResponse(
        List<ServicoResponse> servicos,
        PaginacaoResponse paginacao
) {
    public static ListarServicosResponse toResponse(
            ListarServicosOutput output
    ) {
        List<ServicoResponse> servicos = output.servicos()
                .stream()
                .map(ServicoResponse::toResponse)
                .toList();

        return new ListarServicosResponse(
        		servicos, PaginacaoResponse.toResponse(output.paginacao())
                
        );
    }

    
}
