package br.com.autorepairshop.application.insumo.shared;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.domain.insumo.Insumo;

public record InsumoOutput(
        UUID id,
        String descricao,
        BigDecimal valorReferencia,
        Instant criadoEm,
        Instant atualizadoEm) {

    public static InsumoOutput fromDomain(Insumo insumo) {
        return new InsumoOutput(
                insumo.getId(),
                insumo.getDescricao(),
                insumo.getValorReferencia(),
                insumo.getCriadoEm(),
                insumo.getAtualizadoEm());
    }
}