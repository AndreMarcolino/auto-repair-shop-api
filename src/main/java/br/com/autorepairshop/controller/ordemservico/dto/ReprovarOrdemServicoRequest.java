package br.com.autorepairshop.controller.ordemservico.dto;

import br.com.autorepairshop.application.ordemservico.reprovar.ReprovarOrdemServicoInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record ReprovarOrdemServicoRequest (
		@Schema(description = "Número da OS", example = "OS-2026-900004")
		String numeroOrdemServico,
        @Schema(description = "Código de acesso enviado ao cliente", example = "NPQR6789")
        String codigoVerificacao,
        @Schema(description = "CPF ou CNPJ do cliente", example = "\"98765432100\"", type = "string")
        String cpfCnpj
) {

    public ReprovarOrdemServicoInput toInput() {
        return new ReprovarOrdemServicoInput(
        		numeroOrdemServico,
        		codigoVerificacao,
                cpfCnpj
        );
    }
}


