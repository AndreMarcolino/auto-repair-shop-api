
package br.com.autorepairshop.controller.insumo.dto;

import java.util.List;

import br.com.autorepairshop.application.insumo.listar.ListarInsumosOutput;
import br.com.autorepairshop.controller.shared.dto.PaginacaoResponse;

public record ListarInsumosResponse(
        List<InsumoResponse> insumos,
        PaginacaoResponse paginacao
) {
    public static ListarInsumosResponse toResponse(
            ListarInsumosOutput output
    ) {
        List<InsumoResponse> insumos = output.insumos()
                .stream()
                .map(InsumoResponse::toResponse)
                .toList();

        return new ListarInsumosResponse(
        		insumos, PaginacaoResponse.toResponse(output.paginacao())
                
        );
    }

    
}
