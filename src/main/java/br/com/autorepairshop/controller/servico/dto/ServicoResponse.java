package br.com.autorepairshop.controller.servico.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.application.servico.shared.ServicoOutput;

public record ServicoResponse(
        UUID id,
        String descricao,
        BigDecimal valorReferencia,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static ServicoResponse toResponse(ServicoOutput output) {
        return new ServicoResponse(
                output.id(),
                output.descricao(),
                output.valorReferencia(),
                output.criadoEm(),
                output.atualizadoEm()
        );
    }
}
