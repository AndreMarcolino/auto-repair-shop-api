package br.com.autorepairshop.controller.ordemservico.dto;

import br.com.autorepairshop.application.ordemservico.reprovar.ReprovarOrdemServicoInput;

public record ReprovarOrdemServicoRequest (
		String numeroOrdemServico,
        String codigoVerificacao,
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


