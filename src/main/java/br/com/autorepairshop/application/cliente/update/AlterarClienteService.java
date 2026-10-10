
package br.com.autorepairshop.application.cliente.update;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.Email;
import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;

@Service
public class AlterarClienteService implements AlterarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public AlterarClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public void executar(
            AlterarClienteInput input) {

    	UUID id =  null;
    	try {
    	    id = UUID.fromString(input.id());
    	} catch (IllegalArgumentException | NullPointerException exception) {
    		throw new DomainException(DomainError.CLIENTE_ID_INVALIDO);
    	}

        var tipoPessoa = TipoPessoa.deCodigo(input.tipoPessoa());
        var cpfCnpj = CpfCnpj.criar(tipoPessoa,input.cpfCnpj());
        var email = Email.criar(input.email());

        Optional<Cliente> cliente = clienteRepository.buscarPorId(id);

        if (cliente.isEmpty()) {
        	throw new DomainException(DomainError.CLIENTE_NAO_ENCONTRADO);
        }

        // Só verifica duplicidade de cpf/cnpj mudou.
        if (!cliente.get().getCpfCnpj().equals(cpfCnpj) 
        		&& clienteRepository.existeCpfCnpj(cpfCnpj)) {

            throw new DomainException(DomainError.CPF_CNPJ_JA_CADASTRADO);
        }

       
        cliente.get().atualizarCadastro(
                input.nome(),
                cpfCnpj,
                input.telefone(),
                email);
        
        this.clienteRepository.salvar(cliente.get());
        
    }
}
