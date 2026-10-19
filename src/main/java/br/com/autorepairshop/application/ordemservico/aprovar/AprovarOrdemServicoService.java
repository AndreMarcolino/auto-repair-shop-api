package br.com.autorepairshop.application.ordemservico.aprovar;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.autorepairshop.application.ordemservico.eventos.OrdemServicoAprovada;
import br.com.autorepairshop.application.shared.eventos.EventoPublicador;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;
import jakarta.transaction.Transactional;

@Service
public class AprovarOrdemServicoService implements AprovarOrdemServicoUseCase {

	private final OrdemServicoRepository ordemServicoRepository;
	
	private final ClienteRepository clienteRepository;
	
	private final OrcamentoRepository orcamentoRepository;
	
	private final EventoPublicador eventoPublicador;
	
	private final HistoricoStatusOrdemServicoRepository historicoStatusOrdemServicoRepository;
	
	public AprovarOrdemServicoService(OrdemServicoRepository ordemServicoRepository,
			ClienteRepository clienteRepository,
			OrcamentoRepository orcamentoRepository,
			EventoPublicador eventoPublicador,
			HistoricoStatusOrdemServicoRepository historicoStatusOrdemServicoRepository) {
		this.ordemServicoRepository = ordemServicoRepository;
		this.clienteRepository = clienteRepository;
		this.historicoStatusOrdemServicoRepository = historicoStatusOrdemServicoRepository;
		this.orcamentoRepository = orcamentoRepository;
		this.eventoPublicador = eventoPublicador;
	}
	@Transactional
	public void executar(AprovarOrdemServicoInput input) {

		UUID idOrdemServico = null;
        if(input.idOrdemServico() == null || input.idOrdemServico().isBlank()) {
        	throw new DomainException(DomainError.ID_ORDEM_SERVICO_INVALIDO);
        }
        
        
        try {
        	
        	idOrdemServico = UUID.fromString(input.idOrdemServico());
        } catch (IllegalArgumentException | NullPointerException exception) {
        	throw new DomainException(DomainError.ID_ORDEM_SERVICO_INVALIDO);
        }
		
        var tipoPessoa = TipoPessoa.deCodigo(input.tipoPessoa());
	    var cpfCnpj = CpfCnpj.criar(tipoPessoa,input.cpfCnpj());
	    
	    var cliente = clienteRepository
	            .buscarPorCpfCnpj(cpfCnpj)
	            .orElseThrow(() ->
	                    new DomainException(
	                            DomainError.CLIENTE_NAO_ENCONTRADO));
	    
	    
	    OrdemServico ordemServico = ordemServicoRepository
	            .buscarPorId(idOrdemServico)
	            .orElseThrow(() ->
	                    new DomainException(
	                            DomainError.ORDEM_SERVICO_NAO_ENCONTRADA));
	    
	   
	    if(cliente.getId() != ordemServico.getClienteId()) {
	    	  new DomainException(
                      DomainError.ORDEM_SERVICO_NAO_PERTECEN_AO_CLIENTE);
	    }

	    Orcamento orcamento = orcamentoRepository
	            .buscarPorOrdemServicoId(idOrdemServico)
	            .orElseThrow(() ->
	                    new DomainException(DomainError.ORCAMENTO_NAO_ENCONTRADO));

	    var statusAnterior = ordemServico.getStatus();
	    
	    orcamento.aprovar();
	    ordemServico.aprovar();
	    orcamentoRepository.salvar(orcamento);
	    ordemServicoRepository.salvar(ordemServico);
	    
	    var historico = HistoricoStatusOrdemServico.registrar(
                idOrdemServico,
                statusAnterior,
                ordemServico.getStatus());
	    
	    historicoStatusOrdemServicoRepository.salvar(historico);
	    
	    
	    eventoPublicador.publicar(new OrdemServicoAprovada(ordemServico.getId()));
	}
}
