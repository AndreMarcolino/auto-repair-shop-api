package br.com.autorepairshop.controller.autenticacao.dto;

import br.com.autorepairshop.application.autenticacao.LoginInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @NotBlank(message = "E-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        @Schema(description = "E-mail do funcionário", example = "atendente@auto-repair-shop.com.br")
        String email,

        @NotBlank(message = "Senha é obrigatória.")
        @Schema(description = "Senha", example = "123456")
        String senha
) {

    public LoginInput paraInput() {
        return new LoginInput(email, senha);
    }
}
