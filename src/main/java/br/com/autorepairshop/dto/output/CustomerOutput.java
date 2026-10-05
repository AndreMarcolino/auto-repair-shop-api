package br.com.autorepairshop.dto.output;

public record CustomerOutput(
        Long id,
        String name,
        String cpf,
        String cnpj
) {}