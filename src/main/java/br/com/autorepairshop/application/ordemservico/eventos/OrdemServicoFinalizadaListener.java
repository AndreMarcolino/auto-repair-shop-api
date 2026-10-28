package br.com.autorepairshop.application.ordemservico.eventos;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import br.com.autorepairshop.application.ordemservico.aguardarpagamento.AguardarPagamentoOrdemServicoUseCase;

@Component
public class OrdemServicoFinalizadaListener {

    private final AguardarPagamentoOrdemServicoUseCase aguardarPagamentoOrdemServicoUseCase;

    public OrdemServicoFinalizadaListener(
    		AguardarPagamentoOrdemServicoUseCase aguardarPagamentoOrdemServicoUseCase) {
        this.aguardarPagamentoOrdemServicoUseCase = aguardarPagamentoOrdemServicoUseCase;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoAprovar(OrdemServicoFinalizada evento) {
        aguardarPagamentoOrdemServicoUseCase.executar(evento.ordemServicoId());
    }
}
