package br.com.autorepairshop.application.servico.criar;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.domain.servico.Servico;

public record CriarServicoOutput(
        UUID id,
        String descricao,
        BigDecimal valorReferencia,
        Instant criadoEm
) {
    public static CriarServicoOutput fromDomain(Servico servico) {
        return new CriarServicoOutput(
                servico.getId(),
                servico.getDescricao(),
                servico.getValorReferencia(),
                servico.getCriadoEm());
    }
}
