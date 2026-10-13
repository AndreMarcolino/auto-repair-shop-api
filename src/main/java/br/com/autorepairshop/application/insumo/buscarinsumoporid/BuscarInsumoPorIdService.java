package br.com.autorepairshop.application.insumo.buscarinsumoporid;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.autorepairshop.application.insumo.shared.InsumoOutput;
import br.com.autorepairshop.domain.insumo.Insumo;
import br.com.autorepairshop.domain.insumo.InsumoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class BuscarInsumoPorIdService  implements BuscarInsumoPorIdUseCase {
   
	private final InsumoRepository insumoRepository;

    public BuscarInsumoPorIdService(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }
	
    public Optional<InsumoOutput> executar(String id) {

    	UUID uuid =  null;
    	try {
    	    uuid = UUID.fromString(id);
    	} catch (IllegalArgumentException | NullPointerException exception) {
    		throw new DomainException(DomainError.ID_INSUMO_INVALIDO);
    	}
    	
    	Optional<Insumo> insumo = insumoRepository.buscarPorId(uuid);
        if(insumo.isEmpty()) {
        	return Optional.empty();
        }
       
        return Optional.of(InsumoOutput.fromDomain(insumo.get()));
    }
}
