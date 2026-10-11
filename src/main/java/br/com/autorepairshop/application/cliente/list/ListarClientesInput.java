package br.com.autorepairshop.application.cliente.list;

public record ListarClientesInput(
	    String nome,
	    Short tipoPessoa,
	    String cpfCnpj,
	    String email,
	    Boolean ativo,
	    String telefone,
	    int pagina,
	    int tamanho
	) {}
