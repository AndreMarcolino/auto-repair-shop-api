package br.com.autorepairshop.controller.autenticacao.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
