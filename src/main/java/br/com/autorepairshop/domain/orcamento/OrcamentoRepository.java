package br.com.autorepairshop.domain.orcamento;

import java.util.Optional;
import java.util.UUID;

public interface OrcamentoRepository {

    void salvar(Orcamento orcamento);

    Optional<Orcamento> buscarPorId(UUID id);

    Optional<Orcamento> buscarPorOrdemServicoId(
            UUID ordemServicoId);
}