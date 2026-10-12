package br.com.autorepairshop.domain.veiculo;

import java.util.Optional;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.Pagina;
import br.com.autorepairshop.domain.shared.Paginacao;
import br.com.autorepairshop.domain.shared.valueobject.Placa;

public interface VeiculoRepository {

    Veiculo salvar(Veiculo veiculo);
    Optional<Veiculo> buscarPorId(UUID id);
    boolean existePlaca(Placa placa);
    Pagina<Veiculo> buscar(
    	    String placa,
    	    String marca,
    	    String modelo,
    	    Integer anoFabricacao,
			Paginacao paginacao
    	);
    void excluir(UUID id);
}
