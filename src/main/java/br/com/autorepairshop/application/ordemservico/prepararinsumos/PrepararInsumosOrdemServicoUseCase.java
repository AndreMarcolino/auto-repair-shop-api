package br.com.autorepairshop.application.ordemservico.prepararinsumos;

import java.util.UUID;

public interface PrepararInsumosOrdemServicoUseCase {

    void executar(UUID ordemServicoId);
}
