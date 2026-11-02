
package br.com.autorepairshop.controller.servico.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.servico.listar.ListarServicosInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record ListarServicosRequest(
        @Schema(description = "Filtro por parte da descrição", example = "óleo")
        String descricao,
        @Schema(description = "Valor de referência", example = "120.00")
        BigDecimal valorReferencia,
        @Schema(description = "Número da página (começa em 0)", example = "0")
        int pagina,
        @Schema(description = "Quantidade de itens por página", example = "10")
        int tamanho
) {
    public ListarServicosInput toInput() {
        return new ListarServicosInput(
                descricao,
                valorReferencia
        );
    }
}

