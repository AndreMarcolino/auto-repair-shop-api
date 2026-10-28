package br.com.autorepairshop.domain.orcamento;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class OrcamentoItemInsumo {

    private UUID id;
    private UUID insumoId;
    private String descricao;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;

    public OrcamentoItemInsumo(
            UUID insumoId,
            String descricao,
            BigDecimal quantidade,
            BigDecimal valorUnitario) {

        this.id = UUID.randomUUID();
        this.insumoId = Objects.requireNonNull(insumoId);
        this.descricao = validarDescricao(descricao);
        this.quantidade = validarQuantidade(quantidade);
        this.valorUnitario = validarValor(valorUnitario);
    }

    private String validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException(
                    "A descrição da peça é obrigatória.");
        }
        return descricao.trim();
    }

    private BigDecimal validarQuantidade(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade da peça deve ser maior que zero.");
        }
        return quantidade;
    }

    private BigDecimal validarValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "O valor unitário da peça não pode ser negativo.");
        }
        return valor;
    }

    public void atualizarQuantidade(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
        	  throw new IllegalArgumentException(
                      "A quantidade da peça deve ser maior que zero.");
        }

        this.quantidade = this.quantidade.add(quantidade);
    }
    public UUID getId() {
        return id;
    }

    public UUID getInsumoId() {
        return insumoId;
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
    
    private OrcamentoItemInsumo(
            UUID id,
            UUID insumoId,
            String descricao,
            BigDecimal quantidade,
            BigDecimal valorUnitario) {

        this.id = id;
        this.insumoId = insumoId;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public static OrcamentoItemInsumo recria(
            UUID id,
            UUID insumoId,
            String descricao,
            BigDecimal quantidade,
            BigDecimal valorUnitario) {

        return new OrcamentoItemInsumo(
                id, insumoId, descricao, quantidade, valorUnitario
        );
    }
   
}