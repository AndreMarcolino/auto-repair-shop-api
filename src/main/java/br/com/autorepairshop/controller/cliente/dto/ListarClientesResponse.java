package br.com.autorepairshop.controller.cliente.dto;

import java.util.List;

import br.com.autorepairshop.application.cliente.list.ListarClientesOutput;

public record ListarClientesResponse(
	    List<ClienteResponse> clientes,
	    int pagina,
	    int tamanho,
	    long totalElementos,
	    int totalPaginas
	) {
	
	public static ListarClientesResponse paraResponse(
	        ListarClientesOutput output) {

	    return new ListarClientesResponse(
	        output.clientes().stream()
	            .map(ClienteResponse::paraResponse)
	            .toList(),
	        output.pagina(),
	        output.tamanho(),
	        output.totalRegistros(),
	        output.totalPaginas()
	    );
	}
}
