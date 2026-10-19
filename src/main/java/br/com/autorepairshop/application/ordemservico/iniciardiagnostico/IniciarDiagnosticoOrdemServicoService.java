package br.com.autorepairshop.application.ordemservico.iniciardiagnostico;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.funcionario.Funcionario;
import br.com.autorepairshop.domain.funcionario.FuncionarioRepository;
import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class IniciarDiagnosticoOrdemServicoService implements IniciarDiagnosticoUseCase {

	
	private final OrdemServicoRepository ordemServicoRepository;
	
	private final HistoricoStatusOrdemServicoRepository historicoStatusOrdemServicoRepository;
	
	private final OrcamentoRepository orcamentoRepository;
	
	private final FuncionarioRepository funcionarioRepository;
	
	public IniciarDiagnosticoOrdemServicoService(OrdemServicoRepository ordemServicoRepository,
			HistoricoStatusOrdemServicoRepository historicoStatusOrdemServicoRepository,
			OrcamentoRepository orcamentoRepository,
			FuncionarioRepository funcionarioRepository) {
		this.ordemServicoRepository = ordemServicoRepository;
		this.historicoStatusOrdemServicoRepository = historicoStatusOrdemServicoRepository;
		this.orcamentoRepository = orcamentoRepository;
		this.funcionarioRepository = funcionarioRepository;
	}
	@Override
	@Transactional
	public void executar(IniciarDiagnosticoOrdemServicoInput input) {
		
		UUID idOrdemServico = null;
        if(input.idOrdemServico() == null || input.idOrdemServico().isBlank()) {
        	throw new DomainException(DomainError.ID_ORDEM_SERVICO_INVALIDO);
        }
        
        
        try {
        	
        	idOrdemServico = UUID.fromString(input.idOrdemServico());
        } catch (IllegalArgumentException | NullPointerException exception) {
        	throw new DomainException(DomainError.ID_ORDEM_SERVICO_INVALIDO);
        }
        
        UUID idMecanico = null;
    	try {
        	
        	idMecanico = UUID.fromString(input.idOrdemServico());
        } catch (IllegalArgumentException | NullPointerException exception) {
        	throw new DomainException(DomainError.ID_ORDEM_SERVICO_INVALIDO);
        }
        
    	Funcionario funcionario = funcionarioRepository
    	        .buscarPorId(UUID.fromString(input.mecanicoId()))
    	        .orElseThrow(() ->
    	                new DomainException(DomainError.ID_MECANICO_INVALIDO));
    	
    	if (!funcionario.podeAtenderExecutarServico()) {
    	    throw new DomainException(DomainError.FUNCIONARIO_SEM_PERMISSAO);
    	}
        OrdemServico ordemServico = ordemServicoRepository.buscarPorId(idOrdemServico)
                .orElseThrow(() -> new DomainException(DomainError.OS_NAO_ENCONTRADA));
        
		StatusOrdemServico statusAnterior = ordemServico.getStatus();
		ordemServico.iniciarDiagnostico();
		
		this.ordemServicoRepository.salvar(ordemServico);
		var orcamento = Orcamento.criar(ordemServico.getId(), funcionario.getId());
		this.orcamentoRepository.salvar(orcamento);
		
		var historico = HistoricoStatusOrdemServico.registrar(
                idOrdemServico,
                statusAnterior,
                ordemServico.getStatus());
		
		historicoStatusOrdemServicoRepository.salvar(historico);
		
	}
}
