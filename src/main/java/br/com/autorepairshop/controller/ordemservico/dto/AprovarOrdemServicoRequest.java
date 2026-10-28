package br.com.autorepairshop.controller.ordemservico.dto;

import br.com.autorepairshop.application.ordemservico.aprovar.AprovarOrdemServicoInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record AprovarOrdemServicoRequest(
		@Schema(description = "Número da OS", example = "OS-2026-900003")
		String numeroOrdemServico,
        @Schema(description = "Código de acesso enviado ao cliente", example = "JKLM2345")
        String codigoVerificacao,
        @Schema(description = "CPF ou CNPJ do cliente", example = "\"52998224725\"", type = "string")
        String cpfCnpj
) {

    public AprovarOrdemServicoInput toInput() {
        return new AprovarOrdemServicoInput(
        		numeroOrdemServico,
        		codigoVerificacao,
                cpfCnpj
        );
    }
}