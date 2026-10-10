package br.com.autorepairshop.domain.shared.valueobject;

import java.util.Locale;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

public record CpfCnpj(TipoPessoa tipoPessoa, String cpfCnpj) {

    public static CpfCnpj criar(
            TipoPessoa tipoPessoa,
            String cpfCnpj) {

        if (tipoPessoa == null) {
            throw new DomainException(DomainError.TIPO_PESSOA_INVALIDO);
        }

        if (cpfCnpj == null || cpfCnpj.isBlank()) {
        	throw new DomainException(DomainError.CPF_CNPJ_INVALIDO);
        }

        String numeroLimpo = cpfCnpj.replaceAll("[^a-zA-Z0-9]", "").toUpperCase(Locale.ROOT);

        boolean valido = switch (tipoPessoa) {
            case PESSOA_FISICA -> validarCpf(numeroLimpo);
            case PESSOA_JURIDICA -> validarCnpj(numeroLimpo);
        };

        if (!valido) {
        	throw new DomainException(DomainError.CPF_CNPJ_INVALIDO);
        }

        return new CpfCnpj(tipoPessoa, numeroLimpo);
    }

    private static boolean validarCpf(String cpf) {

        if (!cpf.matches("\\d{11}")) {
            return false;
        }

        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        int primeiroDigito = calcularDigitoCpf(cpf, 9, 10);
        int segundoDigito = calcularDigitoCpf(cpf, 10, 11);

        return primeiroDigito == cpf.charAt(9) - '0'
                && segundoDigito == cpf.charAt(10) - '0';
    }

    private static int calcularDigitoCpf(
            String cpf,
            int quantidade,
            int pesoInicial) {

        int soma = 0;

        for (int i = 0; i < quantidade; i++) {
            soma += (cpf.charAt(i) - '0')
                    * (pesoInicial - i);
        }

        int resto = soma % 11;

        return resto < 2 ? 0 : 11 - resto;
    }

    private static boolean validarCnpj(String cnpj) {

        if (!cnpj.matches("[A-Z0-9]{12}\\d{2}")) {
            return false;
        }

        int primeiroDigito = calcularDigitoCnpj(
                cnpj.substring(0, 12),
                new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});

        String baseComPrimeiroDigito =
                cnpj.substring(0, 12) + primeiroDigito;

        int segundoDigito = calcularDigitoCnpj(
                baseComPrimeiroDigito,
                new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});

        return primeiroDigito == cnpj.charAt(12) - '0'
                && segundoDigito == cnpj.charAt(13) - '0';
    }

    private static int calcularDigitoCnpj(
            String base,
            int[] pesos) {

        int soma = 0;

        for (int i = 0; i < pesos.length; i++) {
            int valor = base.charAt(i) - '0';

            if (Character.isLetter(base.charAt(i))) {
                valor = base.charAt(i) - 48;
            }

            soma += valor * pesos[i];
        }

        int resto = soma % 11;

        return resto < 2 ? 0 : 11 - resto;
    }
}
