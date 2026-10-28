package br.com.autorepairshop.controller.ordemservico.dto;

import br.com.autorepairshop.application.ordemservico.detalharcliente.DetalharOrdemServicoClienteInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record DetalharOrdemServicoClienteRequest (
		@Schema(description = "Número da OS", example = "OS-2026-900003")
		String numeroOrdemServico,
        @Schema(description = "Código de acesso enviado ao cliente", example = "JKLM2345")
        String codigoVerificacao,
        @Schema(description = "CPF ou CNPJ do cliente", example = "\"52998224725\"", type = "string")
        String cpfCnpj
) {

    public DetalharOrdemServicoClienteInput toInput(String idOrdemServico) {
        return new DetalharOrdemServicoClienteInput(
        		numeroOrdemServico,
        		codigoVerificacao,
                cpfCnpj
        );
    }
}
