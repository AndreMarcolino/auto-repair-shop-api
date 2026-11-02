package br.com.autorepairshop.domain.orcamento;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class OrcamentoItemServico {

    private UUID id;
    private UUID servicoId;
    private String descricao;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;

    public OrcamentoItemServico(
            UUID servicoId,
            String descricao,
            BigDecimal quantidade,
            BigDecimal valorUnitario) {

        this.id = UUID.randomUUID();
        this.servicoId = Objects.requireNonNull(servicoId);
        this.descricao = validarDescricao(descricao);
        this.quantidade = validarQuantidade(quantidade);
        this.valorUnitario = validarValor(valorUnitario);
    }

    private String validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException(
                    "A descrição do serviço é obrigatória.");
        }
        return descricao.trim();
    }

    private BigDecimal validarQuantidade(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade do serviço deve ser maior que zero.");
        }
        return quantidade;
    }

    private BigDecimal validarValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "O valor unitário do serviço não pode ser negativo.");
        }
        return valor;
    }
    
    public void atualizarQuantidade(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
        	  throw new IllegalArgumentException(
                      "A quantidade da peça deve ser maior que zero.");
        }

        this.quantidade = quantidade;
    }

    public UUID getId() {
        return id;
    }

    public UUID getServicoId() {
        return servicoId;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return quantidade.multiply(valorUnitario);
    }
    
    private OrcamentoItemServico(
            UUID id,
            UUID servicoId,
            String descricao,
            BigDecimal quantidade,
            BigDecimal valorUnitario) {

        this.id = id;
        this.servicoId = servicoId;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public static OrcamentoItemServico recria(
            UUID id,
            UUID servicoId,
            String descricao,
            BigDecimal quantidade,
            BigDecimal valorUnitario) {

        return new OrcamentoItemServico(
                id,
                servicoId,
                descricao,
                quantidade,
                valorUnitario
        );
    }
}
