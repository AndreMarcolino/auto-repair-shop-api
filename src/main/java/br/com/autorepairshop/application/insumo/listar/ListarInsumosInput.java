package br.com.autorepairshop.application.insumo.listar;

import java.math.BigDecimal;

public record ListarInsumosInput(
        String descricao,
        BigDecimal valorReferencia,
        Integer quantidadeEstoque
) {
}