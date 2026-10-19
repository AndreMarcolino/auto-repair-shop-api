package br.com.autorepairshop.application.ordemservico.reprovar;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;
import jakarta.transaction.Transactional;

@Service
public class ReprovarOrdemServicoService implements ReprovarOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final HistoricoStatusOrdemServicoRepository historicoRepository;

    public ReprovarOrdemServicoService(
            OrdemServicoRepository ordemServicoRepository,
            ClienteRepository clienteRepository,
            OrcamentoRepository orcamentoRepository,
            HistoricoStatusOrdemServicoRepository historicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepository = clienteRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.historicoRepository = historicoRepository;
    }

    @Override
    @Transactional
    public void executar(ReprovarOrdemServicoInput input) {

        if (input.idOrdemServico() == null
                || input.idOrdemServico().isBlank()) {
            throw new DomainException(DomainError.ID_ORDEM_SERVICO_INVALIDO);
        }

        UUID idOrdemServico;

        try {
            idOrdemServico = UUID.fromString(input.idOrdemServico());
        } catch (IllegalArgumentException exception) {
            throw new DomainException(DomainError.ID_ORDEM_SERVICO_INVALIDO);
        }

        var tipoPessoa = TipoPessoa.deCodigo(input.tipoPessoa());
        var cpfCnpj = CpfCnpj.criar(tipoPessoa, input.cpfCnpj());

        var cliente = clienteRepository
                .buscarPorCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new DomainException(
                        DomainError.CLIENTE_NAO_ENCONTRADO));

        var ordemServico = ordemServicoRepository
                .buscarPorId(idOrdemServico)
                .orElseThrow(() -> new DomainException(
                        DomainError.ORDEM_SERVICO_NAO_ENCONTRADA));

        if (!cliente.getId().equals(ordemServico.getClienteId())) {
            throw new DomainException(
                    DomainError.ORDEM_SERVICO_NAO_PERTECEN_AO_CLIENTE);
        }

        var orcamento = orcamentoRepository
                .buscarPorOrdemServicoId(idOrdemServico)
                .orElseThrow(() -> new DomainException(
                        DomainError.ORCAMENTO_NAO_ENCONTRADO));

        var statusAnterior = ordemServico.getStatus();

        orcamento.reprovar();
        ordemServico.rejeitar();

        orcamentoRepository.salvar(orcamento);
        ordemServicoRepository.salvar(ordemServico);

        var historico = HistoricoStatusOrdemServico.registrar(
                idOrdemServico,
                statusAnterior,
                ordemServico.getStatus());

        historicoRepository.salvar(historico);
    }
}