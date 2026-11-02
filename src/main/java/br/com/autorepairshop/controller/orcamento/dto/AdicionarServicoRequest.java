package br.com.autorepairshop.controller.orcamento.dto;

import java.util.List;

import br.com.autorepairshop.application.orcamento.adicionarservico.AdicionarServicoInput;
import br.com.autorepairshop.application.orcamento.shared.ServicoOrcamentoInput;

public record AdicionarServicoRequest(
        List<ServicoRequest> servicos
) {
    public AdicionarServicoInput paraInput(
            String ordemServicoId,
            String uuidUsuario
    ) {
        return new AdicionarServicoInput(
                ordemServicoId,
                servicos.stream()
                        .map(servico -> new ServicoOrcamentoInput(
                                servico.idServico(),
                                servico.quantidade()
                        ))
                        .toList(),
                uuidUsuario
        );
    }
}