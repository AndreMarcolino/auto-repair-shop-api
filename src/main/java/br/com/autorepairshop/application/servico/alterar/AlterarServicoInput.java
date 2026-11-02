package br.com.autorepairshop.application.servico.alterar;

import java.math.BigDecimal;


public record AlterarServicoInput(String id, String descricao, BigDecimal valorReferencia) {
}
