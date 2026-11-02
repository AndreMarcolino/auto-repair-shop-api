package br.com.autorepairshop.application.ordemservico.aguardarpagamento;

import java.util.UUID;

public interface AguardarPagamentoOrdemServicoUseCase {

    void executar(UUID idOrdemServico);
}
