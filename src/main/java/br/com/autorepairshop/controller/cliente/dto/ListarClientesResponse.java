package br.com.autorepairshop.controller.cliente.dto;

import java.util.List;

import br.com.autorepairshop.application.cliente.listar.ListarClientesOutput;
import br.com.autorepairshop.controller.dto.PaginacaoResponse;

public record ListarClientesResponse(
	    List<ClienteResponse> clientes,
	    PaginacaoResponse paginacaoResponse
	) {
	
	public static ListarClientesResponse paraResponse(
	        ListarClientesOutput output) {

	    return new ListarClientesResponse(
	        output.clientes().stream()
	            .map(ClienteResponse::paraResponse)
	            .toList(),
	            PaginacaoResponse.paraResponse(output.paginacao())
	    );
	}
}
