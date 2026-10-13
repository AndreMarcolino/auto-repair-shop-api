package br.com.autorepairshop.application.insumo.buscarinsumoporid;

import java.util.Optional;

import br.com.autorepairshop.application.insumo.shared.InsumoOutput;


public interface BuscarInsumoPorIdUseCase {
	Optional<InsumoOutput> executar(String id);   
}