package br.com.autorepairshop.application.autenticacao;

public record LoginInput(
        String email,
        String senha
) {
}