package br.com.autorepairshop.application.estoque.eventos;

import java.util.UUID;

public record EntradaEstoqueRegistrada(
        UUID insumoId
) {}
