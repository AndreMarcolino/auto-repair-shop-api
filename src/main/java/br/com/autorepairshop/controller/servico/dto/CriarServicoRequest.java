package br.com.autorepairshop.controller.servico.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.servico.criar.CriarServicoInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record CriarServicoRequest(
        @Schema(description = "Descrição do serviço", example = "Higienização do ar-condicionado")
        String descricao,
        @Schema(description = "Valor de referência do serviço", example = "180.00")
        BigDecimal valorReferencia
) {
    public CriarServicoInput toInput() {
        return new CriarServicoInput(descricao, valorReferencia);
    }
}
