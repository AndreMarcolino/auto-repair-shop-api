package br.com.autorepairshop.controller.orcamento.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

public record ServicoRequest(
        @Schema(description = "ID do serviço", example = "33333333-3333-3333-3333-000000000005")
        String idServico,
        @Schema(description = "Quantidade", example = "1")
        BigDecimal quantidade
) {
}