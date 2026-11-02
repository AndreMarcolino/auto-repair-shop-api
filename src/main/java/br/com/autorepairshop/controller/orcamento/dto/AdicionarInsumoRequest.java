package br.com.autorepairshop.controller.orcamento.dto;

import java.util.List;

import br.com.autorepairshop.application.orcamento.adicionarinsumo.AdicionarInsumoInput;
import br.com.autorepairshop.application.orcamento.shared.InsumoOrcamentoInput;

public record AdicionarInsumoRequest(
        List<InsumoRequest> insumos
) {
    public AdicionarInsumoInput paraInput(
            String ordemServicoId,
            String uuidUsuario
    ) {
        return new AdicionarInsumoInput(
                ordemServicoId,
                insumos.stream()
                        .map(insumo -> new InsumoOrcamentoInput(
                                insumo.idInsumo().toString(),
                                insumo.quantidade()
                        ))
                        .toList(),
                uuidUsuario
        );
    }
}