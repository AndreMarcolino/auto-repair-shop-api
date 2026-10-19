package br.com.autorepairshop.application.orcamento.adicionarinsumo;

import java.util.List;

import br.com.autorepairshop.application.orcamento.shared.InsumoOrcamentoInput;

public record AdicionarInsumoInput (String ordemServicoId, List<InsumoOrcamentoInput> insumos, String uuidUsuario) {
	
}

