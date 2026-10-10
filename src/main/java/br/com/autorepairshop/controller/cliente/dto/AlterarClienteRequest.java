package br.com.autorepairshop.controller.cliente.dto;

import br.com.autorepairshop.application.cliente.update.AlterarClienteInput;

public record AlterarClienteRequest(
	    String nome,
	    Short tipoPessoa,
	    String cpfCnpj,
	    String telefone,
	    String email
	) {
	
	public AlterarClienteInput paraInput(String id) {
        return new AlterarClienteInput(
            id,
            nome,
            tipoPessoa,
            cpfCnpj,
            telefone,
            email
        );
    }	
	
}


