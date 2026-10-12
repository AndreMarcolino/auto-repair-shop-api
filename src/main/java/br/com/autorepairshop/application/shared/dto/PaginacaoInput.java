package br.com.autorepairshop.application.shared.dto;

import br.com.autorepairshop.domain.shared.Paginacao;

public record PaginacaoInput(
        int pagina,
        int tamanho) {

    public Paginacao paraPagina() {
        return new Paginacao(pagina, tamanho);
    }
}
