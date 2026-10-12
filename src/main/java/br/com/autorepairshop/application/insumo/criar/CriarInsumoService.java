package br.com.autorepairshop.application.insumo.criar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.insumo.Insumo;
import br.com.autorepairshop.domain.insumo.InsumoRepository;

@Service
public class CriarInsumoService implements CriarInsumoUseCase {

    private final InsumoRepository insumoRepository;

    public CriarInsumoService(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    @Override
    @Transactional
    public CriarInsumoOutput executar(CriarInsumoInput input) {
        Insumo insumo = Insumo.criar(
                input.descricao(),
                input.valorReferencia(),
                input.quantidadeEstoque());

        Insumo salva = insumoRepository.salvar(insumo);

        return CriarInsumoOutput.fromDomain(salva);
    }
}
