
package br.com.autorepairshop.controller.veiculo.dto;

import java.util.List;

import br.com.autorepairshop.application.veiculo.listar.ListarVeiculosOutput;
import br.com.autorepairshop.controller.shared.dto.PaginacaoResponse;

public record ListarVeiculosResponse(
        List<VeiculoResponse> veiculos,
        PaginacaoResponse paginacao
) {
    public static ListarVeiculosResponse toResponse(
            ListarVeiculosOutput output
    ) {
        List<VeiculoResponse> veiculos = output.veiculos()
                .stream()
                .map(VeiculoResponse::toResponse)
                .toList();

        return new ListarVeiculosResponse(
                veiculos,
                PaginacaoResponse.toResponse(output.paginacao())
        );
    }

    
}
