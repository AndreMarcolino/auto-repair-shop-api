package br.com.autorepairshop.controller.servico.dto;

import java.math.BigDecimal;

import br.com.autorepairshop.application.servico.alterar.AlterarServicoInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record AlterarServicoRequest(
	@Schema(description = "Descrição do serviço", example = "Troca de óleo e filtro")
	String descricao,
    @Schema(description = "Valor de referência do serviço", example = "140.00")
    BigDecimal valorReferencia
) {

    public AlterarServicoInput toInput(String id) {
        return new AlterarServicoInput(
            id,
            descricao,
            valorReferencia
        );
    }
}