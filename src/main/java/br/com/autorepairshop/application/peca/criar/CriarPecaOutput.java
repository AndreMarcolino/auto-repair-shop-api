package br.com.autorepairshop.application.peca.criar;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.domain.peca.Peca;

public record CriarPecaOutput(
        UUID id,
        String descricao,
        BigDecimal valorReferencia,
        int quantidadeEstoque,
        Instant criadoEm
) {
    public static CriarPecaOutput fromDomain(Peca peca) {
        return new CriarPecaOutput(
                peca.getId(),
                peca.getDescricao(),
                peca.getValorReferencia(),
                peca.getQuantidadeEstoque(),
                peca.getCriadoEm());
    }
}
