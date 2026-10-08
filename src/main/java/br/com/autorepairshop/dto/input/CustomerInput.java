package br.com.autorepairshop.dto.input;

import org.hibernate.annotations.processing.Pattern;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerInput(
        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must contain at most 150 characters")
        String name,

        
        String cpf,

        
        String cnpj
) {}