package br.com.autorepairshop.domain.insumo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

public class Insumo {

    private final UUID id;
    private String descricao;
    private BigDecimal valorReferencia;
    private int quantidadeEstoque;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    private Insumo(
            UUID id,
            String descricao,
            BigDecimal valorReferencia,
            int quantidadeEstoque,
            Instant criadoEm,
            Instant atualizadoEm) {
        this.id = validarId(id);
        this.descricao = validarDescricao(descricao);
        this.valorReferencia = valorReferencia;
        this.quantidadeEstoque = quantidadeEstoque;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static Insumo criar(
            String descricao,
            BigDecimal valorReferencia,
            Integer quantidadeEstoque) {
        Instant agora = Instant.now();

        
        validarDescricao(descricao);
        validarValorReferencia(valorReferencia);
        validarQuantidadeEstoque(quantidadeEstoque);
        
        return new Insumo(
                UUID.randomUUID(),
                descricao,
                valorReferencia,
                quantidadeEstoque == null ? 0 : quantidadeEstoque,
                agora,
                agora);
    }

    public void atualizarDados(
            String descricao,
            BigDecimal valorReferencia,
            Integer quantidadeEstoque) {
        this.descricao = validarDescricao(descricao);
        this.valorReferencia = validarValorReferencia(valorReferencia);
        this.quantidadeEstoque = validarQuantidadeEstoque(quantidadeEstoque);
        this.atualizadoEm = Instant.now();
    }

    private static UUID validarId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("O identificador do insumo é obrigatório.");
        }
        return id;
    }

    private static String validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new DomainException(DomainError.DESCRICAO_INSUMO_OBRIGATORIA);
        }
        return descricao.trim();
    }

    private static BigDecimal validarValorReferencia(BigDecimal valor) {
        if (valor == null || valor.signum() < 0) {
            throw new DomainException(DomainError.VALOR_REFERENCIA_INSUMO_INVALIDO);
        }
        return valor;
    }

    private static int validarQuantidadeEstoque(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new DomainException(DomainError.QUANTIDADE_ESTOQUE_INVALIDA);
        }
        return quantidade;
    }

    public UUID getId() { return id; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValorReferencia() { return valorReferencia; }
    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public Instant getCriadoEm() { return criadoEm; }
    public Instant getAtualizadoEm() { return atualizadoEm; }

    public static Insumo reconstituir(
            UUID id,
            String descricao,
            BigDecimal valorReferencia,
            int quantidadeEstoque,
            Instant criadoEm,
            Instant atualizadoEm) {
        return new Insumo(id, descricao, valorReferencia, quantidadeEstoque, criadoEm, atualizadoEm);
    }
}
