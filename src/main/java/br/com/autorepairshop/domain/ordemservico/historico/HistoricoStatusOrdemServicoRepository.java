package br.com.autorepairshop.domain.ordemservico.historico;

import java.util.List;
import java.util.UUID;

public interface HistoricoStatusOrdemServicoRepository {

    void salvar(HistoricoStatusOrdemServico historico);

    List<HistoricoStatusOrdemServico> buscarPorOrdemServicoId(
            UUID ordemServicoId);
}