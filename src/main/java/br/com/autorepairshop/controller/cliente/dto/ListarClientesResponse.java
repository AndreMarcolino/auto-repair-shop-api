package br.com.autorepairshop.controller.cliente.dto;

import java.util.List;

import br.com.autorepairshop.application.cliente.listar.ListarClientesOutput;
import br.com.autorepairshop.controller.shared.dto.PaginacaoResponse;

public record ListarClientesResponse(
	    List<ClienteResponse> clientes,
	    PaginacaoResponse paginacao
	) {
	
	public static ListarClientesResponse toResponse(
	        ListarClientesOutput output) {

	    return new ListarClientesResponse(
	        output.clientes().stream()
	            .map(ClienteResponse::toResponse)
	            .toList(),
	            PaginacaoResponse.toResponse(output.paginacao())
	    );
	}
}
