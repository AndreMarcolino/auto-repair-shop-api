package br.com.autorepairshop.controller.cliente.dto;

import br.com.autorepairshop.application.cliente.criar.CriarClienteInput;

public record CriarClienteRequest(
	    String nome,
	    Short tipoPessoa,
	    String cpfCnpj,
	    String telefone,
	    String email
	) {
	    public CriarClienteInput toInput() {
	        return new CriarClienteInput(
	            nome,
	            tipoPessoa,
	            cpfCnpj,
	            telefone,
	            email
	        );
	    }
	}
