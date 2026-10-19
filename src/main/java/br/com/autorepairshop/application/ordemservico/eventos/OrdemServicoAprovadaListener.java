package br.com.autorepairshop.application.ordemservico.eventos;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import br.com.autorepairshop.application.ordemservico.prepararinsumos.PrepararInsumosOrdemServicoUseCase;

@Component
public class OrdemServicoAprovadaListener {

    private final PrepararInsumosOrdemServicoUseCase prepararInsumos;

    public OrdemServicoAprovadaListener(
            PrepararInsumosOrdemServicoUseCase prepararInsumos) {
        this.prepararInsumos = prepararInsumos;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoAprovar(OrdemServicoAprovada evento) {
        prepararInsumos.executar(evento.ordemServicoId());
    }
}
