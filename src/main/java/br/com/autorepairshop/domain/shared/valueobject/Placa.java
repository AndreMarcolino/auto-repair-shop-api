package br.com.autorepairshop.domain.shared.valueobject;

import java.util.Locale;
import java.util.regex.Pattern;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

public record Placa(String placa) {

    private static final Pattern PADRAO_PLACA = Pattern.compile(
        "^[A-Z]{3}(?:[0-9]{4}|[0-9][A-Z][0-9]{2})$"
    );

    public static Placa criar(String valor) {

        if (valor == null || valor.isBlank()) {
            throw new DomainException(DomainError.PLACA_INVALIDA);
        }

        String placaNormalizada = valor
            .trim()
            .replace("-", "")
            .replace(" ", "")
            .toUpperCase(Locale.ROOT);

        if (!PADRAO_PLACA.matcher(placaNormalizada).matches()) {
            throw new DomainException(DomainError.PLACA_INVALIDA);
        }

        return new Placa(placaNormalizada);
    }
}
