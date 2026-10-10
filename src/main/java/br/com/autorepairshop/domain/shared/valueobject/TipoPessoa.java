package br.com.autorepairshop.domain.shared.valueobject;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

public enum TipoPessoa {
    PESSOA_FISICA((short) 1),
    PESSOA_JURIDICA((short) 2);

    private final short codigo;

    TipoPessoa(short codigo) {
        this.codigo = codigo;
    }

    public short getCodigo() {
        return codigo;
    }

    public static TipoPessoa deCodigo(Short codigo) {
        if (codigo == null) {
        	throw new DomainException(DomainError.TIPO_PESSOA_INVALIDO);
        }

        for (TipoPessoa tipo : values()) {
            if (tipo.codigo == codigo) {
                return tipo;
            }
        }

        throw new DomainException(DomainError.TIPO_PESSOA_INVALIDO);
    }
}