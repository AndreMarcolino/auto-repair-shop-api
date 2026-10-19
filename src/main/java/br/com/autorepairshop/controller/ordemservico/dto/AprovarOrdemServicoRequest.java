package br.com.autorepairshop.controller.ordemservico.dto;

import br.com.autorepairshop.application.ordemservico.aprovar.AprovarOrdemServicoInput;

public record AprovarOrdemServicoRequest(
        Short tipoPessoa,
        String cpfCnpj
) {

    public AprovarOrdemServicoInput toInput(String idOrdemServico) {
        return new AprovarOrdemServicoInput(
                idOrdemServico,
                tipoPessoa,
                cpfCnpj
        );
    }
}