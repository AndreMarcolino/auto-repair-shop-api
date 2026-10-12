
package br.com.autorepairshop.controller.servico.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.servico.listar.ListarServicosInput;

public record ListarServicosRequest(
        String descricao,
        BigDecimal valorReferencia,
        int pagina,
        int tamanho
) {
    public ListarServicosInput toInput() {
        return new ListarServicosInput(
                descricao,
                valorReferencia
        );
    }
}

