
package br.com.autorepairshop.controller.veiculo.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.autorepairshop.application.veiculo.listar.ListarVeiculosOutput;
import br.com.autorepairshop.application.veiculo.shared.VeiculoOutput;
import br.com.autorepairshop.controller.shared.dto.PaginacaoResponse;

public record ListarVeiculosResponse(
        List<VeiculoItemResponse> veiculos,
        PaginacaoResponse paginacao
) {
    public static ListarVeiculosResponse toResponse(
            ListarVeiculosOutput output
    ) {
        List<VeiculoItemResponse> veiculos = output.veiculos()
                .stream()
                .map(VeiculoItemResponse::toResponse)
                .toList();

        return new ListarVeiculosResponse(
                veiculos,
                PaginacaoResponse.toResponse(output.paginacao())
        );
    }

    public record VeiculoItemResponse(
            UUID id,
            String placa,
            String marca,
            String modelo,
            Integer anoFabricacao,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        public static VeiculoItemResponse toResponse(VeiculoOutput output) {
            return new VeiculoItemResponse(
                    output.id(),
                    output.placa(),
                    output.marca(),
                    output.modelo(),
                    output.anoFabricacao(),
                    output.criadoEm(),
                    output.atualizadoEm()
            );
        }
    }
}
