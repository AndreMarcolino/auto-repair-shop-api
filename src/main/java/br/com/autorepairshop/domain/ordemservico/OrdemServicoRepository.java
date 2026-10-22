package br.com.autorepairshop.domain.ordemservico;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoRepository {

    void salvar(OrdemServico ordemServico);
    Optional<OrdemServico> buscarPorId(UUID id);
    List<OrdemServico> buscarPorStatus(StatusOrdemServico status);
    long proximoNumero();
    List<OrdemServico> buscarPorStatus(List<StatusOrdemServico> status);
    Optional<OrdemServico> buscarPoNumero(String numero);
}