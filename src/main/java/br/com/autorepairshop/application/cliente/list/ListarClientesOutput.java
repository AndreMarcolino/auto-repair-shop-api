package br.com.autorepairshop.application.cliente.list;

import java.util.List;

import br.com.autorepairshop.application.cliente.shared.ClienteOutput;

public record ListarClientesOutput(
	    List<ClienteOutput> clientes,
	    int pagina,
	    int tamanho,
	    long totalRegistros,
	    int totalPaginas
	) {
	    
	}