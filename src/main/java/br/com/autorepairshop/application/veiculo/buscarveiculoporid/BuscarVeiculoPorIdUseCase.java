package br.com.autorepairshop.application.veiculo.buscarveiculoporid;

import java.util.Optional;

import br.com.autorepairshop.application.veiculo.shared.VeiculoOutput;


public interface BuscarVeiculoPorIdUseCase {
	Optional<VeiculoOutput> executar(String id);   
}