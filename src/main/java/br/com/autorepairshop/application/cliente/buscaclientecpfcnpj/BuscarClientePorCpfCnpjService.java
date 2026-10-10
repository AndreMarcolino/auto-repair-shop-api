package br.com.autorepairshop.application.cliente.buscaclientecpfcnpj;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.autorepairshop.application.cliente.shared.ClienteOutput;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;

@Service
public class BuscarClientePorCpfCnpjService implements BuscarClientePorCpfCnpjUseCase {
   
	private final ClienteRepository clienteRepository;

    public BuscarClientePorCpfCnpjService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
	
    public Optional<ClienteOutput> executar(Short tipoPessoaInformada, String cpfCnpjInformado) {

        var tipoPessoa = TipoPessoa.deCodigo(tipoPessoaInformada);
        var cpfCnpj = CpfCnpj.criar(tipoPessoa, cpfCnpjInformado);

        var cliente = clienteRepository.buscarPorCpfCnpj(cpfCnpj);
        return cliente.map(ClienteOutput::fromDomain);

    }
}
