package br.com.autorepairshop.application.cliente.buscarclienteporid;

import java.util.Optional;

import br.com.autorepairshop.application.cliente.shared.ClienteOutput;


public interface BuscarClientePorIdUseCase {
	Optional<ClienteOutput> executar(String id);   
}