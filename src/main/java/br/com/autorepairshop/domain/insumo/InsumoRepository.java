package br.com.autorepairshop.domain.insumo;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.Pagina;
import br.com.autorepairshop.domain.shared.Paginacao;

public interface InsumoRepository {

    Insumo salvar(Insumo insumo);
    Optional<Insumo> buscarPorId(UUID id);
    Pagina<Insumo> buscar(
            String descricao,
            BigDecimal valorReferencia,
            Integer quantidadeEstoque,
            Paginacao paginacao);
    void excluir(UUID id);
}
