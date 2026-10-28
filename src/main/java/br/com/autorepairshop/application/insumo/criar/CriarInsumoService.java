package br.com.autorepairshop.application.insumo.criar;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.application.estoque.registrarentrada.RegistrarEntradaEstoqueInput;
import br.com.autorepairshop.application.estoque.registrarentrada.RegistrarEntradaEstoqueUseCase;
import br.com.autorepairshop.domain.insumo.Insumo;
import br.com.autorepairshop.domain.insumo.InsumoRepository;

@Service
public class CriarInsumoService implements CriarInsumoUseCase {

    private final InsumoRepository insumoRepository;
    
    private final RegistrarEntradaEstoqueUseCase registarEntradaEstoqueUseCase;

    public CriarInsumoService(InsumoRepository insumoRepository,
    		RegistrarEntradaEstoqueUseCase registrarEntradaEstoqueUseCase) {
        this.insumoRepository = insumoRepository;
        this.registarEntradaEstoqueUseCase = registrarEntradaEstoqueUseCase;
    }

    @Override
    @Transactional
    public CriarInsumoOutput executar(CriarInsumoInput input) {
        Insumo insumo = Insumo.criar(
                input.descricao(),
                input.valorReferencia());

        var insumoSalvo = insumoRepository.salvar(insumo);

        registarEntradaEstoqueUseCase.executar(new RegistrarEntradaEstoqueInput(insumoSalvo.getId().toString(), BigDecimal.ZERO));
        
        return CriarInsumoOutput.fromDomain(insumoSalvo);
    }
}
