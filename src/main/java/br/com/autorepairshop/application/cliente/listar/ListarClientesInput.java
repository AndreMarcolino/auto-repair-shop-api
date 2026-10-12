package br.com.autorepairshop.application.cliente.listar;

public record ListarClientesInput(
	    String nome,
	    Short tipoPessoa,
	    String cpfCnpj,
	    String email,
	    Boolean ativo,
	    String telefone
	) {}
