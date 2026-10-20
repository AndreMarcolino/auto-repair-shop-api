package br.com.autorepairshop.application.orcamento.adicionarinsumo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.application.orcamento.shared.InsumoOrcamentoInput;
import br.com.autorepairshop.domain.insumo.Insumo;
import br.com.autorepairshop.domain.insumo.InsumoRepository;
import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class AdicionarInsumoService implements AdicionarInsumoUseCase {

	
	private final OrcamentoRepository orcamentoRepository;
	
	private final InsumoRepository insumoRepository;

	
	public AdicionarInsumoService(OrcamentoRepository orcamentoRepository,
			InsumoRepository insumoRepository) {
		this.orcamentoRepository = orcamentoRepository;
		this.insumoRepository = insumoRepository;
	}
	@Override
	@Transactional
	public void executar(AdicionarInsumoInput input) {
		
	 	
        ValidaEntrada(input);
        var idOrdemServico = UUID.fromString(input.ordemServicoId());
        List<UUID> idsInsumos = input.insumos().stream().map(insumo -> UUID.fromString(insumo.idInsumo())).toList();  
        var insumos = this.insumoRepository.buscarPorIds(idsInsumos);
        if (insumos.size() != idsInsumos.size()) {
        	 Set<UUID> idsEncontrados = insumos.stream()
        	            .map(Insumo::getId)
        	            .collect(Collectors.toSet());

        	    Set<UUID> idsNaoEncontrados = new HashSet<>(idsEncontrados);
        	    idsNaoEncontrados.removeAll(idsEncontrados);

        	    throw DomainException.comParametro(DomainError.INSUMOS_NAO_ENCONTRADOS,idsNaoEncontrados);
        }
        
        
        
        Orcamento orcamento = orcamentoRepository.buscarPorOrdemServicoId(idOrdemServico)
                .orElseThrow(() -> new DomainException(DomainError.ORCAMENTO_NAO_ENCONTRADO));
        
        for (Insumo insumo : insumos) {
            InsumoOrcamentoInput insumoInput = input.insumos().stream()
                    .filter(i -> UUID.fromString(i.idInsumo()).equals(insumo.getId()))
                    .findFirst()
                    .orElseThrow();

            orcamento.adicionarInsumo(
                    insumo.getId(),
                    insumo.getDescricao(),
                    insumoInput.quantidade(),
                    insumo.getValorReferencia()
            );
        }
		
		this.orcamentoRepository.salvar(orcamento);

	}
	
	private void ValidaEntrada(AdicionarInsumoInput input) {
		if (input.ordemServicoId() == null || input.ordemServicoId().isBlank()) {
            throw new DomainException(DomainError.ORDEM_SERVICO_OBRIGATORIA);
        }

        if (input.insumos() == null || input.insumos().isEmpty()) {
        	throw new DomainException(DomainError.INFORME_AO_MENOS_UM_INSUMO);
        }

        if (input.insumos().stream().anyMatch(
                insumo -> insumo == null
                        || insumo.idInsumo() == null
                        || insumo.idInsumo().isBlank())) {
            throw new DomainException(DomainError.IDS_INSUMOS_NAO_PODEM_SER_NULOS_OU_VAZIO);
        }

        if (input.uuidUsuario() == null || input.uuidUsuario().isBlank()) {
        	throw new DomainException(DomainError.ID_DO_USUARIO_OBRIGATORIO);
        }
		
    
	    try {
          UUID.fromString(input.ordemServicoId());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new DomainException(DomainError.ID_INSUMO_INVALIDO);
        }
		
		
	    try {
	    	input.insumos().stream().map(insumo -> UUID.fromString(insumo.idInsumo())).toList(); 
	    } catch (IllegalArgumentException | NullPointerException e) {
	        throw new DomainException(DomainError.ID_INSUMO_INVALIDO);
	    }
	    
	}
}
