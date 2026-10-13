package br.com.autorepairshop.application.servico.buscarservicoporid;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.autorepairshop.application.servico.shared.ServicoOutput;
import br.com.autorepairshop.domain.servico.Servico;
import br.com.autorepairshop.domain.servico.ServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class BuscarServicoPorIdService  implements BuscarServicoPorIdUseCase {
   
	private final ServicoRepository servicoRepository;

    public BuscarServicoPorIdService(ServicoRepository insumoRepository) {
        this.servicoRepository = insumoRepository;
    }
	
    public Optional<ServicoOutput> executar(String id) {

    	UUID uuid =  null;
    	try {
    	    uuid = UUID.fromString(id);
    	} catch (IllegalArgumentException | NullPointerException exception) {
    		throw new DomainException(DomainError.ID_SERVICO_INVALIDO);
    	}
    	
    	Optional<Servico> insumo = servicoRepository.buscarPorId(uuid);
        if(insumo.isEmpty()) {
        	return Optional.empty();
        }
       
        return Optional.of(ServicoOutput.fromDomain(insumo.get()));
    }
}
