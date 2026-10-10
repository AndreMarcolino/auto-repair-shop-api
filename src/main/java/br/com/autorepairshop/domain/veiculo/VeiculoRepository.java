package br.com.autorepairshop.domain.veiculo;

import br.com.autorepairshop.domain.shared.valueobject.Placa;

public interface VeiculoRepository {

    Veiculo salvar(Veiculo veiculo);
    boolean existePlaca(Placa placa);
}
