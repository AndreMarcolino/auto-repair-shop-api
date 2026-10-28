package br.com.autorepairshop.domain.shared;

import java.util.List;

public class DomainException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final List<DomainError> erros;

    public DomainException(DomainError... erros) {
        super(erros.length > 0 ? erros[0].getMensagem() : null);
        this.erros = List.of(erros);
    }

    private DomainException(DomainError erro, Object... parametros) {
        super(erro.getMensagem(parametros));
        this.erros = List.of(erro);
    }

    public static DomainException comParametro(
            DomainError erro,
            Object... parametros) {

        return new DomainException(erro, parametros);
    }

    public List<DomainError> getErros() {
        return erros;
    }
}