package br.com.autorepairshop.application.ordemservico.prepararinsumos;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class PrepararInsumosOrdemServicoService
        implements PrepararInsumosOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrcamentoRepository orcamentoRepository;
   
    public PrepararInsumosOrdemServicoService(
            OrdemServicoRepository ordemServicoRepository,
            OrcamentoRepository orcamentoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.orcamentoRepository = orcamentoRepository;
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

        if (ordemServico.getStatus()
                != br.com.autorepairshop.domain.ordemservico.StatusOrdemServico.APROVADA) {
            return;
        }

        orcamentoRepository
                .buscarPorOrdemServicoId(idOrdemServico)
                .orElseThrow();

                    ordemServicoRepository.salvar(ordemServico);
    }
}