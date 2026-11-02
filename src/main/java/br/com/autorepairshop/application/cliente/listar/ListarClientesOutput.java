package br.com.autorepairshop.application.cliente.listar;

import java.util.List;

import br.com.autorepairshop.application.cliente.shared.ClienteOutput;
import br.com.autorepairshop.application.shared.dto.PaginacaoOutput;

public record ListarClientesOutput(
	    List<ClienteOutput> clientes,
        PaginacaoOutput paginacao
	) {
	    
	}