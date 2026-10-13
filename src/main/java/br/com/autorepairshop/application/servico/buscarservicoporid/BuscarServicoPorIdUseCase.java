package br.com.autorepairshop.application.servico.buscarservicoporid;

import java.util.Optional;

import br.com.autorepairshop.application.servico.shared.ServicoOutput;


public interface BuscarServicoPorIdUseCase {
	Optional<ServicoOutput> executar(String id);   
}