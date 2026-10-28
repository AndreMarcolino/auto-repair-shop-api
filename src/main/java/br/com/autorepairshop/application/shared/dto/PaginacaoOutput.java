package br.com.autorepairshop.application.shared.dto;

import br.com.autorepairshop.domain.shared.Pagina;

public record PaginacaoOutput(
        int pagina,
        int tamanho,
        long totalElementos,
        int totalPaginas) {
	
	public static PaginacaoOutput de(Pagina<?> page) {
        return new PaginacaoOutput(
                page.pagina(),
                page.tamanho(),
                page.totalElementos(),
                page.totalPaginas());
    }
}
