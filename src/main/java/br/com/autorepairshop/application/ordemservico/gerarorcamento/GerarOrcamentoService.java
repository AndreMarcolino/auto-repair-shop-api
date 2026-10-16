package br.com.autorepairshop.application.ordemservico.gerarorcamento;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class GerarOrcamentoService implements GerarOrcamentoUseCase {

	
	private final OrdemServicoRepository ordemServicoRepository;
	
	private final HistoricoStatusOrdemServicoRepository historicoStatusOrdemServicoRepository;
	
	public GerarOrcamentoService(OrdemServicoRepository ordemServicoRepository,
			HistoricoStatusOrdemServicoRepository historicoStatusOrdemServicoRepository) {
		this.ordemServicoRepository = ordemServicoRepository;
		this.historicoStatusOrdemServicoRepository = historicoStatusOrdemServicoRepository;
	}
	@Override
	@Transactional
	public String executar(GerarOrcamentoInput input) {
		
		UUID idOrdemServico = null;
        if(input.idOrdemServico() == null || input.idOrdemServico().isBlank()) {
        	throw new DomainException(DomainError.ID_ORDEM_SERVICO_INVALIDO);
        }
        
        
        try {
        	
        	idOrdemServico = UUID.fromString(input.idOrdemServico());
        } catch (IllegalArgumentException | NullPointerException exception) {
        	throw new DomainException(DomainError.ID_ORDEM_SERVICO_INVALIDO);
        }
        
        
        
        OrdemServico ordemServico = ordemServicoRepository.buscarPorId(idOrdemServico)
                .orElseThrow(() -> new DomainException(DomainError.OS_NAO_ENCONTRADA));
        
		StatusOrdemServico statusAnterior = ordemServico.getStatus();
		ordemServico.aguardarEnvioOrcamento();
		
		this.ordemServicoRepository.salvar(ordemServico);
		historicoStatusOrdemServicoRepository.salvar(
                HistoricoStatusOrdemServico.registrar(
                        idOrdemServico,
                        statusAnterior,
                        ordemServico.getStatus()));
        
		
		
		
		return ordemServico.getId().toString();

	}
}
