package br.com.autorepairshop.domain.shared.valueobject;

import java.util.regex.Pattern;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

public record Email(String email) {

    private static final Pattern PADRAO_EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public static Email criar(String endereco) {

        if (endereco == null || endereco.isBlank()) {
        	throw new DomainException(DomainError.EMAIL_INVALIDO);
        }

        String enderecoNormalizado = endereco.trim();

        if (!PADRAO_EMAIL.matcher(enderecoNormalizado).matches()) {
            throw new DomainException(DomainError.EMAIL_INVALIDO);
        }

        return new Email(enderecoNormalizado);
    }
}
