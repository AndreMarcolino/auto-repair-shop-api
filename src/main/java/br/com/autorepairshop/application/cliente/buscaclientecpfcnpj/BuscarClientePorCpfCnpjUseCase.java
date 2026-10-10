package br.com.autorepairshop.application.cliente.buscaclientecpfcnpj;

import java.util.Optional;

import br.com.autorepairshop.application.cliente.shared.ClienteOutput;


public interface BuscarClientePorCpfCnpjUseCase {
	Optional<ClienteOutput> executar(Short tipoPessoaInformada, String cpfCnpjInformado);   
}