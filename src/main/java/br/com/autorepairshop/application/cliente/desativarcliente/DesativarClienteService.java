
package br.com.autorepairshop.application.cliente.desativarcliente;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;



@Service
public class DesativarClienteService implements DesativarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public DesativarClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public void executar(String idCliente) {

    	UUID id =  null;
    	try {
    	    id = UUID.fromString(idCliente);
    	} catch (IllegalArgumentException | NullPointerException exception) {
    		throw new DomainException(DomainError.ID_CLIENTE_INVALIDO);
    	}

        
        Optional<Cliente> cliente = clienteRepository.buscarPorId(id);

        if (cliente.isEmpty()) {
        	throw new DomainException(DomainError.CLIENTE_NAO_ENCONTRADO);
        }

        cliente.get().desativar();
        
        this.clienteRepository.salvar(cliente.get());
        
    }
}
