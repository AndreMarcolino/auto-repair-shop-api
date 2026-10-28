package br.com.autorepairshop.application.insumo.criar;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.domain.insumo.Insumo;

public record CriarInsumoOutput(
        UUID id,
        String descricao,
        BigDecimal valorReferencia,
        Instant criadoEm
) {
    public static CriarInsumoOutput fromDomain(Insumo insumo) {
        return new CriarInsumoOutput(
                insumo.getId(),
                insumo.getDescricao(),
                insumo.getValorReferencia(),
                insumo.getCriadoEm());
    }
}
