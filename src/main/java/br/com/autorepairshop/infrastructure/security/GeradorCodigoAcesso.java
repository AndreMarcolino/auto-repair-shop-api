package br.com.autorepairshop.infrastructure.security;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class GeradorCodigoAcesso {

    private static final String CARACTERES =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final SecureRandom secureRandom = new SecureRandom();

    public String gerar() {
        StringBuilder codigo = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            int indice = secureRandom.nextInt(CARACTERES.length());
            codigo.append(CARACTERES.charAt(indice));
        }

        return codigo.toString();
    }
}