package br.com.autorepairshop.application.estoque.eventos;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import br.com.autorepairshop.application.ordemservico.liberaraguardandoinsumos.LiberarOrdensServicoAguardandoInsumosUseCase;


@Component
public class EntradaEstoqueRegistradaListener {

    private final LiberarOrdensServicoAguardandoInsumosUseCase liberarOrdensServicoAguardandoInsumosUseCase;

    public EntradaEstoqueRegistradaListener(
    		LiberarOrdensServicoAguardandoInsumosUseCase liberarOrdensServicoAguardandoInsumosUseCase) {
        this.liberarOrdensServicoAguardandoInsumosUseCase = liberarOrdensServicoAguardandoInsumosUseCase;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoRegistrarEntrada(EntradaEstoqueRegistrada event) {
    	liberarOrdensServicoAguardandoInsumosUseCase.executar();
    }
}