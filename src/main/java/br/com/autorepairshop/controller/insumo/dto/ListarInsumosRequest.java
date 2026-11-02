
package br.com.autorepairshop.controller.insumo.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.insumo.listar.ListarInsumosInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record ListarInsumosRequest(
        @Schema(description = "Filtro por parte da descrição", example = "Filtro de óleo")
        String descricao,
        @Schema(description = "Valor de referência", example = "35.00")
        BigDecimal valorReferencia,
        @Schema(description = "Quantidade em estoque")
        Integer quantidadeEstoque,
        @Schema(description = "Número da página (começa em 0)", example = "0")
        int pagina,
        @Schema(description = "Quantidade de itens por página", example = "10")
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

