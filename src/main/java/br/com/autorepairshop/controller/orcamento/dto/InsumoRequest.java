package br.com.autorepairshop.controller.orcamento.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

public record InsumoRequest(
        @Schema(description = "ID do insumo", example = "44444444-4444-4444-4444-000000000001")
        String idInsumo,
        @Schema(description = "Quantidade", example = "4")
        BigDecimal quantidade
) {
}
