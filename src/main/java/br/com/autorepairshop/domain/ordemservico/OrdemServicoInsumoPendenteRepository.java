package br.com.autorepairshop.domain.ordemservico;

import java.util.List;
import java.util.UUID;

public interface OrdemServicoInsumoPendenteRepository {

    void salvar(OrdemServicoInsumoPendente pendente);

    List<OrdemServicoInsumoPendente> buscarPorOrdemServicoId(
            UUID ordemServicoId);

    void excluirPorOrdemServicoId(UUID ordemServicoId);
}