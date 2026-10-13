package br.com.autorepairshop.domain.servico;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.Pagina;
import br.com.autorepairshop.domain.shared.Paginacao;
import br.com.autorepairshop.domain.shared.repository.DomainRepository;

public interface ServicoRepository extends DomainRepository<Servico, UUID> {

    Servico salvar(Servico servico);
    Pagina<Servico> buscar(
    	    String descricao,
    	    BigDecimal valorReferencia,
    	    Paginacao paginacao
    	);
   
    
}
