package br.com.autorepairshop.domain.veiculo;

import java.util.Optional;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.valueobject.Placa;

public interface VeiculoRepository {

    Veiculo salvar(Veiculo veiculo);
    Optional<Veiculo> buscarPorId(UUID id);
    boolean existePlaca(Placa placa);
}
