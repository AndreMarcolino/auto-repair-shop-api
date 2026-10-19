package br.com.autorepairshop.controller.autenticacao.dto;

import br.com.autorepairshop.application.autenticacao.LoginInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "E-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        String email,

        @NotBlank(message = "Senha é obrigatória.")
        String senha
) {

    public LoginInput paraInput() {
        return new LoginInput(email, senha);
    }
}
