
package br.com.autorepairshop.application.orcamento.adicionarservico;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.application.orcamento.shared.ServicoOrcamentoInput;
import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.servico.Servico;
import br.com.autorepairshop.domain.servico.ServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class AdicionarServicoService implements AdicionarServicoUseCase {

    private final OrcamentoRepository orcamentoRepository;
    private final ServicoRepository servicoRepository;

    public AdicionarServicoService(
            OrcamentoRepository orcamentoRepository,
            ServicoRepository servicoRepository) {
        this.orcamentoRepository = orcamentoRepository;
        this.servicoRepository = servicoRepository;
    }

    @Override
    @Transactional
    public void executar(AdicionarServicoInput input) {

        validarEntrada(input);

        UUID idOrdemServico = UUID.fromString(input.ordemServicoId());

        List<UUID> idsServicos = input.servicos().stream()
                .map(servico -> UUID.fromString(servico.idServico()))
                .toList();

        List<Servico> servicos = servicoRepository.buscarPorIds(idsServicos);

        if (servicos.size() != idsServicos.size()) {

            Set<UUID> idsEncontrados = servicos.stream()
                    .map(Servico::getId)
                    .collect(Collectors.toSet());

            Set<UUID> idsNaoEncontrados = new HashSet<>(idsServicos);
            idsNaoEncontrados.removeAll(idsEncontrados);

            throw DomainException.comParametro(
                    DomainError.IDS_SERVICOS_NAO_PODEM_SER_NULOS_OU_VAZIO,
                    idsNaoEncontrados
            );
        }

        Orcamento orcamento = orcamentoRepository
                .buscarPorOrdemServicoId(idOrdemServico)
                .orElseThrow(() ->
                        new DomainException(DomainError.ORCAMENTO_NAO_ENCONTRADO));

        for (Servico servico : servicos) {

            ServicoOrcamentoInput servicoInput = input.servicos().stream()
                    .filter(s -> UUID.fromString(s.idServico())
                            .equals(servico.getId()))
                    .findFirst()
                    .orElseThrow();

            orcamento.adicionarServico(
                    servico.getId(),
                    servico.getDescricao(),
                    servicoInput.quantidade(),
                    servico.getValorReferencia()
            );
        }

        orcamentoRepository.salvar(orcamento);
    }

    private void validarEntrada(AdicionarServicoInput input) {

        
        if (input.ordemServicoId() == null
                || input.ordemServicoId().isBlank()) {
            throw new DomainException(DomainError.ORDEM_SERVICO_OBRIGATORIA);
        }

        if (input.servicos() == null || input.servicos().isEmpty()) {
            throw new DomainException(DomainError.INFORME_AO_MENOS_UM_SERVICO);
        }

        if (input.servicos().stream().anyMatch(
                servico -> servico == null
                        || servico.idServico() == null
                        || servico.idServico().isBlank())) {
            throw new DomainException(
                    DomainError.IDS_SERVICOS_NAO_PODEM_SER_NULOS_OU_VAZIO);
        }

        try {
            UUID.fromString(input.ordemServicoId());

            input.servicos().stream()
                    .map(servico -> UUID.fromString(servico.idServico()))
                    .toList();

        } catch (IllegalArgumentException | NullPointerException e) {
            throw new DomainException(DomainError.ID_SERVICO_INVALIDO);
        }
    }
}
