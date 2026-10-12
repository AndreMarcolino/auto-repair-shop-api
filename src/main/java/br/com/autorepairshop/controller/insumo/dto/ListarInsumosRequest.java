
package br.com.autorepairshop.controller.insumo.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.insumo.listar.ListarInsumosInput;

public record ListarInsumosRequest(
        String descricao,
        BigDecimal valorReferencia,
        Integer quantidadeEstoque,
        int pagina,
        int tamanho
) {
    public ListarInsumosInput toInput() {
        return new ListarInsumosInput(
                descricao,
                valorReferencia,
                quantidadeEstoque
        );
    }
}

