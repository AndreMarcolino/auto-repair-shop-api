package br.com.autorepairshop.application.cliente.create;

public record CriarClienteInput(
        String nome,
        Short tipoPessoa,
        String cpfCnpj,
        String telefone,
        String email) {
}