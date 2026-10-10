package br.com.autorepairshop.application.veiculo.shared;

import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.domain.veiculo.Veiculo;

public record VeiculoOutput(
    UUID id,
    String placa,
    String marca,
    String modelo,
    Integer anoFabricacao,
    Instant criadoEm,
    Instant atualizadoEm
) {
    public static VeiculoOutput fromDomain(Veiculo veiculo) {
        return new VeiculoOutput(
            veiculo.getId(),
            veiculo.getPlaca().placa(),
            veiculo.getMarca(),
            veiculo.getModelo(),
            veiculo.getAnoFabricacao(),
            veiculo.getCriadoEm(),
            veiculo.getAtualizadoEm()
        );
    }
}