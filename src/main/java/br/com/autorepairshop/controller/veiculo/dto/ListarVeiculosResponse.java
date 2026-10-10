
package br.com.autorepairshop.controller.veiculo.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.autorepairshop.application.veiculo.lista.ListarVeiculosOutput;
import br.com.autorepairshop.application.veiculo.shared.VeiculoOutput;

public record ListarVeiculosResponse(
        List<VeiculoItemResponse> veiculos,
        int pagina,
        int tamanho,
        long totalElementos,
        int totalPaginas
) {
    public static ListarVeiculosResponse paraResponse(
            ListarVeiculosOutput output
    ) {
        List<VeiculoItemResponse> veiculos = output.veiculos()
                .stream()
                .map(VeiculoItemResponse::paraResponse)
                .toList();

        return new ListarVeiculosResponse(
                veiculos,
                output.pagina(),
                output.tamanho(),
                output.totalElementos(),
                output.totalPaginas()
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
        public static VeiculoItemResponse paraResponse(VeiculoOutput output) {
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
