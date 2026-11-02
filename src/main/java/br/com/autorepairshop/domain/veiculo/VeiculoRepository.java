package br.com.autorepairshop.domain.veiculo;

import java.util.UUID;

import br.com.autorepairshop.domain.shared.Pagina;
import br.com.autorepairshop.domain.shared.Paginacao;
import br.com.autorepairshop.domain.shared.repository.DomainRepository;
import br.com.autorepairshop.domain.shared.valueobject.Placa;

public interface VeiculoRepository extends DomainRepository<Veiculo, UUID> {

    Veiculo salvar(Veiculo veiculo);
    boolean existePlaca(Placa placa);
    Pagina<Veiculo> buscar(
    	    String placa,
    	    String marca,
    	    String modelo,
    	    Integer anoFabricacao,
			Paginacao paginacao
    	);
}
