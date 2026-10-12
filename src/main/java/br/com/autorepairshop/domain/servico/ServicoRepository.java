package br.com.autorepairshop.domain.servico;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.Pagina;
import br.com.autorepairshop.domain.shared.Paginacao;

public interface ServicoRepository {

    Servico salvar(Servico servico);
    Optional<Servico> buscarPorId(UUID id);
    void excluir(UUID id);
    Pagina<Servico> buscar(
    	    String descricao,
    	    BigDecimal valorReferencia,
    	    Paginacao paginacao
    	);
    
}
