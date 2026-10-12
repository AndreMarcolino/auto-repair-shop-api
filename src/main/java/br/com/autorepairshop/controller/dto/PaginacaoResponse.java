package br.com.autorepairshop.controller.dto;

import br.com.autorepairshop.application.shared.dto.PaginacaoOutput;

public record PaginacaoResponse(int pagina,
int tamanho,
long totalElementos,
int totalPaginas) {

public static PaginacaoResponse paraResponse(PaginacaoOutput paginacao) {
return new PaginacaoResponse(paginacao.pagina(),paginacao.tamanho(), paginacao.totalElementos(),paginacao.totalPaginas());
}
}