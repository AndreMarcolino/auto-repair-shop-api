package br.com.autorepairshop.application.ordemservico.aguardarpagamento;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class AguardarPagamentoOrdemServicoService
        implements AguardarPagamentoOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final HistoricoStatusOrdemServicoRepository historicoRepository;

    public AguardarPagamentoOrdemServicoService(
            OrdemServicoRepository ordemServicoRepository,
            HistoricoStatusOrdemServicoRepository historicoRepository) {

        this.ordemServicoRepository = ordemServicoRepository;
        this.historicoRepository = historicoRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executar(UUID idOrdemServico) {
    	if (idOrdemServico == null) {
        	throw new DomainException(
                    DomainError.ID_ORDEM_SERVICO_INVALIDO);
        }
        OrdemServico ordemServico = ordemServicoRepository
                .buscarPorId(idOrdemServico)
                .orElseThrow(() ->
                        new DomainException(DomainError.OS_NAO_ENCONTRADA));

        StatusOrdemServico statusAnterior = ordemServico.getStatus();

        ordemServico.aguardarPagamento();

        ordemServicoRepository.salvar(ordemServico);

        var historico = HistoricoStatusOrdemServico.registrar(
        		idOrdemServico,
                statusAnterior,
                ordemServico.getStatus());

        historicoRepository.salvar(historico);
    }
}
