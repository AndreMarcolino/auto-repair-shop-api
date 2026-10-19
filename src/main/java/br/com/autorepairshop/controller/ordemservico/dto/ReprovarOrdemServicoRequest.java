package br.com.autorepairshop.controller.ordemservico.dto;

import br.com.autorepairshop.application.ordemservico.reprovar.ReprovarOrdemServicoInput;

public record ReprovarOrdemServicoRequest (
        Short tipoPessoa,
        String cpfCnpj
) {

    public ReprovarOrdemServicoInput toInput(String idOrdemServico) {
        return new ReprovarOrdemServicoInput(
                idOrdemServico,
                tipoPessoa,
                cpfCnpj
        );
    }
}


