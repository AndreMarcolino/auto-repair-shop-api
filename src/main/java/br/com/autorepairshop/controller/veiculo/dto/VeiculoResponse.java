package br.com.autorepairshop.controller.veiculo.dto;

import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.application.veiculo.shared.VeiculoOutput;

public record VeiculoResponse(
        UUID id,
        String placa,
        String marca,
        String modelo,
        Integer anoFabricacao,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static VeiculoResponse toResponse(VeiculoOutput output) {
        return new VeiculoResponse(
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