package br.com.autorepairshop.application.insumo.listar;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.application.insumo.shared.InsumoOutput;
import br.com.autorepairshop.application.shared.dto.PaginacaoInput;
import br.com.autorepairshop.application.shared.dto.PaginacaoOutput;
import br.com.autorepairshop.domain.insumo.Insumo;
import br.com.autorepairshop.domain.insumo.InsumoRepository;
import br.com.autorepairshop.domain.shared.Pagina;

@Service
public class ListarInsumosService implements ListarInsumosUseCase {

    private final InsumoRepository insumoRepository;

    public ListarInsumosService(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ListarInsumosOutput executar(
            ListarInsumosInput input,
            PaginacaoInput paginacaoInput) {

        Pagina<Insumo> resultado = insumoRepository.buscar(
                input.descricao(),
                input.valorReferencia(),
                input.quantidadeEstoque(),
                paginacaoInput.toPaginacao());

        List<InsumoOutput> insumos = resultado.itens().stream()
                .map(InsumoOutput::fromDomain)
                .toList();

        return new ListarInsumosOutput(
                insumos,
                PaginacaoOutput.de(resultado));
    }
}