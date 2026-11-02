package br.com.autorepairshop.application.autenticacao;

public record LoginOutput(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}