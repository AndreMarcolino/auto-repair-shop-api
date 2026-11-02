package br.com.autorepairshop.application.cliente.buscarclienteporid;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.autorepairshop.application.cliente.shared.ClienteOutput;
import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class BuscarClientePorIdService  implements BuscarClientePorIdUseCase {
	
	private final ClienteRepository clienteRepository;

    public BuscarClientePorIdService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
	
    public Optional<ClienteOutput> executar(String id) {

    	UUID uuid =  null;
    	try {
    	    uuid = UUID.fromString(id);
    	} catch (IllegalArgumentException | NullPointerException exception) {
    		throw new DomainException(DomainError.ID_CLIENTE_INVALIDO);
    	}
    	
    	Optional<Cliente> cliente = clienteRepository.buscarPorId(uuid);
        if(cliente.isEmpty()) {
        	return Optional.empty();
        }
       
        return Optional.of(ClienteOutput.fromDomain(cliente.get()));
    }
}
