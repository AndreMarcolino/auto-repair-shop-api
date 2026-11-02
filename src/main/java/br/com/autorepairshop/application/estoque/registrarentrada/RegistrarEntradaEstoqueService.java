package br.com.autorepairshop.application.estoque.registrarentrada;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.application.estoque.eventos.EntradaEstoqueRegistrada;
import br.com.autorepairshop.application.shared.eventos.EventoPublicador;
import br.com.autorepairshop.domain.estoque.Estoque;
import br.com.autorepairshop.domain.estoque.EstoqueRepository;

@Service
public class RegistrarEntradaEstoqueService implements RegistrarEntradaEstoqueUseCase {

    private final EstoqueRepository estoqueRepository;
    
    private final EventoPublicador eventoPublicador;

    public RegistrarEntradaEstoqueService(
            EstoqueRepository estoqueRepository,
            EventoPublicador eventoPublicador) {
        this.estoqueRepository = estoqueRepository;
        this.eventoPublicador = eventoPublicador;
    }

    @Override
    @Transactional
    public void executar(RegistrarEntradaEstoqueInput input) {
        var estoqueExistente = estoqueRepository
                .buscarParaAtualizacao(UUID.fromString(input.insumoId()));

        if (estoqueExistente.isPresent()) {
            Estoque estoque = estoqueExistente.get();
            estoque.adicionar(input.quantidade());
            estoqueRepository.salvar(estoque);
            eventoPublicador.publicar(new EntradaEstoqueRegistrada(estoque.getInsumoId()));
            return;
        }

        Estoque estoque = Estoque.criar(UUID.fromString(input.insumoId()));

        if (input.quantidade().signum() > 0) {
            estoque.adicionar(input.quantidade());
        }

        estoqueRepository.salvar(estoque);
        
        eventoPublicador.publicar(new EntradaEstoqueRegistrada(estoque.getInsumoId()));
    }
}