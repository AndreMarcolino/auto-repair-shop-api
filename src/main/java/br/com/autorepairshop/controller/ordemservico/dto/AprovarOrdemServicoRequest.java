package br.com.autorepairshop.controller.ordemservico.dto;

import br.com.autorepairshop.application.ordemservico.aprovar.AprovarOrdemServicoInput;

public record AprovarOrdemServicoRequest(
		String numeroOrdemServico,
        String codigoVerificacao,
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