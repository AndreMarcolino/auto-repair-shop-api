package br.com.autorepairshop.controller.ordemservico.dto;

import br.com.autorepairshop.application.ordemservico.detalharcliente.DetalharOrdemServicoClienteInput;

public record DetalharOrdemServicoClienteRequest (
		String numeroOrdemServico,
        String codigoVerificacao,
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
