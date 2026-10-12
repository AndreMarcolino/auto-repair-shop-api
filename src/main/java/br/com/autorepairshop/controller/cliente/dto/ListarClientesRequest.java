package br.com.autorepairshop.controller.cliente.dto;

import br.com.autorepairshop.application.cliente.listar.ListarClientesInput;

public record ListarClientesRequest(
	    String nome,
	    Short tipoPessoa,
	    String cpfCnpj,
	    String email,
	    String telefone,
	    Boolean ativo,
	    int pagina,
        int tamanho
	) {
	
	 public ListarClientesInput toInput() {
	        return new ListarClientesInput(
	            nome,
	            tipoPessoa,
	            cpfCnpj,
	            email,
	            ativo,
	            telefone
	        );
	    }
}
