package br.com.autorepairshop.application.ordemservico.listar;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;

@Service
public class ListarOrdensServicoService implements ListarOrdensServicoUseCase {

	private static final List<StatusOrdemServico> STATUS_LISTAGEM =
	        List.of(StatusOrdemServico.values());
    private final OrdemServicoRepository ordemServicoRepository;

    public ListarOrdensServicoService(
            OrdemServicoRepository ordemServicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
    }

    @Transactional(readOnly = true)
    public List<ListarOrdensServicoOutput> executar() {

        List<OrdemServico> ordensServico =
                ordemServicoRepository.buscarPorStatus(STATUS_LISTAGEM);

        return ordensServico.stream()
                .sorted(
                        Comparator.comparing(
                                (OrdemServico ordem) -> ordem.getStatus().ordinal()
                        ).thenComparing(OrdemServico::getDataAbertura)
                )
                .map(this::paraOutput)
                .toList();
    }

    private ListarOrdensServicoOutput paraOutput(OrdemServico ordem) {
        return new ListarOrdensServicoOutput(
                ordem.getId(),
                ordem.getNumero(),
                ordem.getClienteId(),
                ordem.getVeiculoId(),
                ordem.getStatus(),
                ordem.getDataAbertura()
        );
    }
}