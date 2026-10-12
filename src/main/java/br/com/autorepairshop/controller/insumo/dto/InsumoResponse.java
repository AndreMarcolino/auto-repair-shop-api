package br.com.autorepairshop.controller.insumo.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.application.insumo.shared.InsumoOutput;

public record InsumoResponse(
        UUID id,
        String descricao,
        BigDecimal valorReferencia,
        Integer quantidadeEstoque,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static InsumoResponse toResponse(InsumoOutput output) {
        return new InsumoResponse(
                output.id(),
                output.descricao(),
                output.valorReferencia(),
                output.quantidadeEstoque(),
                output.criadoEm(),
                output.atualizadoEm()
        );
    }
}