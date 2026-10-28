
package br.com.autorepairshop.application.ordemservico.liberaraguardandoinsumos;

import java.util.Comparator;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import br.com.autorepairshop.domain.estoque.Estoque;
import br.com.autorepairshop.domain.estoque.EstoqueRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoInsumoPendente;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoInsumoPendenteRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;

@Service
public class LiberarOrdensServicoAguardandoInsumosService
        implements LiberarOrdensServicoAguardandoInsumosUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final EstoqueRepository estoqueRepository;
    private final OrdemServicoInsumoPendenteRepository pendenteRepository;
    private final HistoricoStatusOrdemServicoRepository historicoRepository;
    private final TransactionTemplate transactionTemplate;

    public LiberarOrdensServicoAguardandoInsumosService(
            OrdemServicoRepository ordemServicoRepository,
            EstoqueRepository estoqueRepository,
            OrdemServicoInsumoPendenteRepository pendenteRepository,
            HistoricoStatusOrdemServicoRepository historicoRepository,
            PlatformTransactionManager transactionManager) {

        this.ordemServicoRepository = ordemServicoRepository;
        this.estoqueRepository = estoqueRepository;
        this.pendenteRepository = pendenteRepository;
        this.historicoRepository = historicoRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(
                TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Override
    public void executar() {
        var ordens = ordemServicoRepository
                .buscarPorStatus(StatusOrdemServico.AGUARDANDO_PECAS);

        for (OrdemServico ordem : ordens) {
            UUID ordemServicoId = ordem.getId();

            transactionTemplate.executeWithoutResult(status ->
                    liberarSeHouverEstoque(ordemServicoId));
        }
    }

    private void liberarSeHouverEstoque(UUID ordemServicoId) {
        var ordemServico = ordemServicoRepository
                .buscarPorId(ordemServicoId)
                .orElse(null);

        if (ordemServico == null
                || ordemServico.getStatus() != StatusOrdemServico.AGUARDANDO_PECAS) {
            return;
        }

        var pendencias = obterOsAguardandoInsumos(ordemServico);

        if (pendencias.isEmpty()) {
            return;
        }

        var estoques = new java.util.ArrayList<Estoque>();

        for (OrdemServicoInsumoPendente pendencia : pendencias) {
            var estoqueOptional = estoqueRepository
                    .buscarParaAtualizacao(pendencia.getInsumoId());

            if (estoqueOptional.isEmpty()) {
                return;
            }

            var estoque = estoqueOptional.get();

            if (!estoque.possuiDisponibilidade(
                    pendencia.getQuantidadeNecessaria())) {
                return;
            }

            estoques.add(estoque);
        }

        for (int i = 0; i < pendencias.size(); i++) {
            var pendencia = pendencias.get(i);
            var estoque = estoques.get(i);

            estoque.baixar(pendencia.getQuantidadeNecessaria());
            estoqueRepository.salvar(estoque);
        }

        var statusAnterior = ordemServico.getStatus();

        ordemServico.liberarAposReposicaoDePecas();
        ordemServicoRepository.salvar(ordemServico);

        pendenteRepository.excluirPorOrdemServicoId(ordemServicoId);

        var historico = HistoricoStatusOrdemServico.registrar(
                ordemServicoId,
                statusAnterior,
                ordemServico.getStatus());

        historicoRepository.salvar(historico);
    }

    private java.util.List<OrdemServicoInsumoPendente> obterOsAguardandoInsumos(
            OrdemServico ordemServico) {

        return pendenteRepository
                .buscarPorOrdemServicoId(ordemServico.getId())
                .stream()
                .sorted(Comparator.comparing(
                        pendencia -> pendencia.getInsumoId().toString()))
                .toList();
    }
}
