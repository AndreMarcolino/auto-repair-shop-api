package br.com.autorepairshop.domain.shared;

public record Paginacao(
        int pagina,
        int tamanho) {

    public Paginacao {
        if (pagina < 0) {
            throw new DomainException(DomainError.PAGINACAO_NEGATIVA);
        }

        if (tamanho < 1) {
        	 throw new DomainException(DomainError.TAMANHO_PAGINA);
        }
    }
}
