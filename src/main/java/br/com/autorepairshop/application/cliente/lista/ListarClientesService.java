package br.com.autorepairshop.application.cliente.lista;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.com.autorepairshop.application.cliente.shared.ClienteOutput;
import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.Email;
import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;

@Service
public class ListarClientesService implements ListarClientesUseCase {

    private final ClienteRepository clienteRepository;

    public ListarClientesService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ListarClientesOutput executar(ListarClientesInput input) {
        Pageable pageable = PageRequest.of(
            input.pagina(),
            input.tamanho(),
            Sort.by("nome").ascending()
        );

        Optional<TipoPessoa> tipoPessoa = Optional.ofNullable(null);
        Optional<Email> email = Optional.ofNullable(null);
        
        if(input.tipoPessoa() != null) {
        	tipoPessoa = Optional.ofNullable(TipoPessoa.deCodigo(input.tipoPessoa()));	
        }
        
        Optional<CpfCnpj> cpfCnpj = tipoPessoa
        	    .map(tipo -> CpfCnpj.criar(tipo, input.cpfCnpj()));
        
        
        if(input.email() != null && !input.email().isBlank()) {
        	email = Optional.ofNullable(Email.criar(input.email())); 
        }
        
      
        Page<Cliente> resultado = clienteRepository.buscar(
            input.nome(),
            tipoPessoa.map(TipoPessoa::getCodigo).orElse(null),
            cpfCnpj.map(CpfCnpj::cpfCnpj).orElse(null),
            email.map(Email::email).orElse(null),
            input.telefone(),
            input.ativo(),
            pageable
        );

        List<ClienteOutput> clientes = resultado
        	    .map(ClienteOutput::fromDomain)
        	    .getContent();

        return new ListarClientesOutput(
            clientes,
            resultado.getNumber(),
            resultado.getSize(),
            resultado.getTotalElements(),
            resultado.getTotalPages()
        );
    }
}