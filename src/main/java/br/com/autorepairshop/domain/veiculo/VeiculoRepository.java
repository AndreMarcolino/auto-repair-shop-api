package br.com.autorepairshop.domain.veiculo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.autorepairshop.domain.shared.valueobject.Placa;

public interface VeiculoRepository {

    Veiculo salvar(Veiculo veiculo);
    Optional<Veiculo> buscarPorId(UUID id);
    boolean existePlaca(Placa placa);
    Page<Veiculo> buscar(
    	    String placa,
    	    String marca,
    	    String modelo,
    	    Integer anoFabricacao,
    	    Pageable pageable
    	);
}
