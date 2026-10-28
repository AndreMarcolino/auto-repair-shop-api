package br.com.autorepairshop.application.servico.listar;

import java.math.BigDecimal;

public record ListarServicosInput(
	String descricao, 
	BigDecimal valorReferencia
) {
}