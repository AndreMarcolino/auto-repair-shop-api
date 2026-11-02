package br.com.autorepairshop.domain.shared;


import java.util.List;

public record Pagina<T>(
        List<T> itens,
        int pagina,
        int tamanho,
        long totalElementos,
        int totalPaginas) {

    public Pagina {
        itens = List.copyOf(itens);
    }
}