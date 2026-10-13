package br.com.autorepairshop.domain.insumo;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.Pagina;
import br.com.autorepairshop.domain.shared.Paginacao;
import br.com.autorepairshop.domain.shared.repository.DomainRepository;

public interface InsumoRepository extends DomainRepository<Insumo, UUID> {

    Insumo salvar(Insumo insumo);
    Pagina<Insumo> buscar(
            String descricao,
            BigDecimal valorReferencia,
            Integer quantidadeEstoque,
            Paginacao paginacao);
}
