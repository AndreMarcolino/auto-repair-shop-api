
package br.com.autorepairshop.application.orcamento.adicionarservico;

import java.util.List;

import br.com.autorepairshop.application.orcamento.shared.ServicoOrcamentoInput;

public record AdicionarServicoInput(
		String ordemServicoId, List<ServicoOrcamentoInput> servicos, String uuidUsuario
) {
}
