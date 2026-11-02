package br.com.autorepairshop.domain.estoque;

import java.util.Optional;
import java.util.UUID;

public interface EstoqueRepository {

    void salvar(Estoque estoque);

    Optional<Estoque> buscarPorInsumoId(UUID insumoId);

    Optional<Estoque> buscarParaAtualizacao(UUID insumoId);
}