package br.com.autorepairshop.application.veiculo.buscarveiculoporid;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.autorepairshop.application.veiculo.shared.VeiculoOutput;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.veiculo.Veiculo;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;

@Service
public class BuscarVeiculoPorIdService  implements BuscarVeiculoPorIdUseCase {
   
	private final VeiculoRepository veiculoRepository;

    public BuscarVeiculoPorIdService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }
	
    public Optional<VeiculoOutput> executar(String id) {

    	UUID uuid =  null;
    	try {
    	    uuid = UUID.fromString(id);
    	} catch (IllegalArgumentException | NullPointerException exception) {
    		throw new DomainException(DomainError.ID_VEICULO_INVALIDO);
    	}
    	
    	Optional<Veiculo> veiculo = veiculoRepository.buscarPorId(uuid);
        if(veiculo.isEmpty()) {
        	return Optional.empty();
        }
       
        return Optional.of(VeiculoOutput.fromDomain(veiculo.get()));
    }
}
