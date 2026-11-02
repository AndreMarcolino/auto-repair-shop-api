package br.com.autorepairshop.application.servico.shared;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.autorepairshop.domain.servico.Servico;

public record ServicoOutput (
	    UUID id,
	    String descricao,
	    BigDecimal valorReferencia,
	    Instant criadoEm,
	    Instant atualizadoEm)
	 {
	    public static ServicoOutput fromDomain(Servico servico) {
	        return new ServicoOutput(
	            servico.getId(),
	            servico.getDescricao(),
	            servico.getValorReferencia(),
	            servico.getCriadoEm(),
	            servico.getAtualizadoEm()
	            
	        );
	    }
	}