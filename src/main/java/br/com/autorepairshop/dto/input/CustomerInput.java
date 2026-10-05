package br.com.autorepairshop.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerInput(
        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must contain at most 150 characters")
        String name,

        @Pattern(
                regexp = "^$|^[0-9]{11}$",
                message = "CPF must contain exactly 11 digits"
        )
        String cpf,

        @Pattern(
                regexp = "^$|^[A-Za-z0-9]{12}[0-9]{2}$",
                message = "CNPJ must contain 14 alphanumeric characters, with the last two numeric"
        )
        String cnpj
) {}