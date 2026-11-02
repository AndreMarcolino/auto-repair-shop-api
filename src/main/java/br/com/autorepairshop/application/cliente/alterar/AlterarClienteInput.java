package br.com.autorepairshop.application.cliente.alterar;

public record AlterarClienteInput(
		String id,
        String nome,
        Short tipoPessoa,
        String cpfCnpj,
        String telefone,
        String email) {
}