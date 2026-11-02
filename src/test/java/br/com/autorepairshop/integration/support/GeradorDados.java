package br.com.autorepairshop.integration.support;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Gera dados de teste válidos e únicos (CPF, CNPJ, placa, e-mail...).
 *
 * Todos os testes de integração compartilham o mesmo banco, e CPF/CNPJ/placa são únicos no sistema.
 * Por isso cada teste gera seus próprios dados, para não depender da ordem de execução.
 * Os dígitos verificadores seguem o mesmo algoritmo validado pela aplicação (value object CpfCnpj).
 */
public final class GeradorDados {

    private static final int[] PESOS_CNPJ_1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_CNPJ_2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private GeradorDados() {
    }

    public static String cpfValido() {
        while (true) {
            int[] d = new int[11];
            for (int i = 0; i < 9; i++) {
                d[i] = digitoAleatorio();
            }
            d[9] = digitoCpf(d, 9, 10);
            d[10] = digitoCpf(d, 10, 11);

            String cpf = comoTexto(d);
            if (cpf.chars().distinct().count() > 1) {
                return cpf;
            }
        }
    }

    public static String cnpjValido() {
        int[] d = new int[14];
        for (int i = 0; i < 12; i++) {
            d[i] = digitoAleatorio();
        }
        d[12] = digitoCnpj(d, PESOS_CNPJ_1);
        d[13] = digitoCnpj(d, PESOS_CNPJ_2);
        return comoTexto(d);
    }

    /** Placa no padrão antigo: três letras e quatro números (ABC1234). */
    public static String placaAntiga() {
        return letras(3) + numeros(4);
    }

    /** Placa no padrão Mercosul: ABC1D23. */
    public static String placaMercosul() {
        return letras(3) + numeros(1) + letras(1) + numeros(2);
    }

    public static String emailUnico() {
        return "cliente." + sufixoUnico() + "@teste.com.br";
    }

    public static String descricaoUnica(String base) {
        return base + " " + sufixoUnico();
    }

    public static String uuidInexistente() {
        return UUID.randomUUID().toString();
    }

    private static String sufixoUnico() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private static int digitoAleatorio() {
        return ThreadLocalRandom.current().nextInt(10);
    }

    private static String letras(int quantidade) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < quantidade; i++) {
            texto.append((char) ('A' + ThreadLocalRandom.current().nextInt(26)));
        }
        return texto.toString();
    }

    private static String numeros(int quantidade) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < quantidade; i++) {
            texto.append(digitoAleatorio());
        }
        return texto.toString();
    }

    private static int digitoCpf(int[] d, int quantidade, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += d[i] * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static int digitoCnpj(int[] d, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += d[i] * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static String comoTexto(int[] digitos) {
        StringBuilder texto = new StringBuilder();
        for (int digito : digitos) {
            texto.append(digito);
        }
        return texto.toString();
    }
}
