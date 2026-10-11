package br.com.autorepairshop.domain.servico;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

public class Servico {

    private final UUID id;
    private String descricao;
    private BigDecimal valorReferencia;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    private Servico(
            UUID id,
            String descricao,
            BigDecimal valorReferencia,
            Instant criadoEm,
            Instant atualizadoEm) {
        this.id = validarId(id);
        this.descricao = validarDescricao(descricao);
        this.valorReferencia = validarValorReferencia(valorReferencia);
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static Servico criar(String descricao, BigDecimal valorReferencia) {
        Instant agora = Instant.now();

        return new Servico(
                UUID.randomUUID(),
                descricao,
                valorReferencia,
                agora,
                agora);
    }

    public void atualizarDados(String descricao, BigDecimal valorReferencia) {
        this.descricao = validarDescricao(descricao);
        this.valorReferencia = validarValorReferencia(valorReferencia);
        this.atualizadoEm = Instant.now();
    }

    private static UUID validarId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("O identificador do serviço é obrigatório.");
        }
        return id;
    }

    private static String validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new DomainException(DomainError.DESCRICAO_SERVICO_OBRIGATORIA);
        }
        return descricao.trim();
    }

    private static BigDecimal validarValorReferencia(BigDecimal valor) {
        if (valor == null || valor.signum() < 0) {
            throw new DomainException(DomainError.VALOR_REFERENCIA_SERVICO_INVALIDO);
        }
        return valor;
    }

    public UUID getId() { return id; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValorReferencia() { return valorReferencia; }
    public Instant getCriadoEm() { return criadoEm; }
    public Instant getAtualizadoEm() { return atualizadoEm; }

    public static Servico reconstituir(
            UUID id,
            String descricao,
            BigDecimal valorReferencia,
            Instant criadoEm,
            Instant atualizadoEm) {
        return new Servico(id, descricao, valorReferencia, criadoEm, atualizadoEm);
    }
}
