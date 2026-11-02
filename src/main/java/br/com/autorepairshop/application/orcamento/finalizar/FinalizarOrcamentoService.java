package br.com.autorepairshop.application.orcamento.finalizar;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class FinalizarOrcamentoService implements FinalizarOrcamentoUseCase {

    private final OrcamentoRepository orcamentoRepository;
    
    private final OrdemServicoRepository ordemServicoRepository;
    
    private final HistoricoStatusOrdemServicoRepository historicoStatusOrdemServicoRepository;

    public FinalizarOrcamentoService(
            OrcamentoRepository orcamentoRepository,
            OrdemServicoRepository ordemServicoRepository,
            HistoricoStatusOrdemServicoRepository historicoStatusOrdemServicoRepository) {
        this.orcamentoRepository = orcamentoRepository;
        this.ordemServicoRepository = ordemServicoRepository;
        this.historicoStatusOrdemServicoRepository =historicoStatusOrdemServicoRepository;
    }

    @Override
    @Transactional
    public void executar(FinalizarOrcamentoInput input) {

        validarEntrada(input);

        UUID ordemServicoId = UUID.fromString(input.ordemServicoId());

        Orcamento orcamento = orcamentoRepository
                .buscarPorOrdemServicoId(ordemServicoId)
                .orElseThrow(() ->
                        new DomainException(DomainError.ORCAMENTO_NAO_ENCONTRADO));

        var ordemServico = this.ordemServicoRepository.buscarPorId(ordemServicoId)
        		.orElseThrow(() ->
                new DomainException(DomainError.ORDEM_SERVICO_NAO_ENCONTRADA));
     
        
        var statusAnterior = ordemServico.getStatus();
        
        orcamento.finalizarElaboracao(input.diagnostico());
        ordemServico.enviarParaAprovacaoCliente();
        
    
        orcamentoRepository.salvar(orcamento);
	    ordemServicoRepository.salvar(ordemServico);
	    
        var historico = HistoricoStatusOrdemServico.registrar(
                ordemServico.getId(),
                statusAnterior,
                ordemServico.getStatus());
	    
	    historicoStatusOrdemServicoRepository.salvar(historico);
		
    }

    private void validarEntrada(FinalizarOrcamentoInput input) {

        if (input == null) {
            throw new DomainException(DomainError.ORDEM_SERVICO_OBRIGATORIA);
        }

        if (input.ordemServicoId() == null
                || input.ordemServicoId().isBlank()) {
            throw new DomainException(DomainError.ORDEM_SERVICO_OBRIGATORIA);
        }

        try {
            UUID.fromString(input.ordemServicoId());
        } catch (IllegalArgumentException e) {
            throw new DomainException(DomainError.ID_SERVICO_INVALIDO);
        }

        if (input.diagnostico() == null || input.diagnostico().isBlank()) {
            throw new DomainException(DomainError.DIAGNOSTICO_OBRIGATORIO);
        }
    }
}
