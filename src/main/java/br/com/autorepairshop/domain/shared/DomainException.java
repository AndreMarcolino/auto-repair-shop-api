package br.com.autorepairshop.domain.shared;

import java.util.List;

public class DomainException extends RuntimeException {


	private static final long serialVersionUID = 1L;
	private final List<DomainError> erros;

    public DomainException(DomainError... erros) {
        super(erros.length > 0 ? erros[0].getMensagem() : null);
        this.erros = List.of(erros);
    }

    public List<DomainError> getErros() {
        return erros;
    }
}
