package br.com.autorepairshop.domain.shared;

public class CredenciaisInvalidasException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CredenciaisInvalidasException() {
        super("E-mail ou senha inválidos.");
    }
}
