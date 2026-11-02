package br.com.autorepairshop.application.ordemservico.efetuarentrega;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.funcionario.Funcionario;
import br.com.autorepairshop.domain.funcionario.FuncionarioRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class EfetuarEntregaOrdemServicoService
        implements EfetuarEntregaUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final HistoricoStatusOrdemServicoRepository historicoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public EfetuarEntregaOrdemServicoService(
            OrdemServicoRepository ordemServicoRepository,
            HistoricoStatusOrdemServicoRepository historicoRepository,
            FuncionarioRepository funcionarioRepository) {

        this.ordemServicoRepository = ordemServicoRepository;
        this.historicoRepository = historicoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    @Transactional
    public void executar(EfetuarEntregaInput input) {

        UUID idOrdemServico = validarUuid(
                input.idOrdemServico(),
                DomainError.ID_ORDEM_SERVICO_INVALIDO);

        UUID idAtendente = validarUuid(
                input.atendenteId(),
                DomainError.ID_ATENDENTE_INVALIDO);

        Funcionario funcionario = funcionarioRepository
                .buscarPorId(idAtendente)
                .orElseThrow(() ->
                        new DomainException(
                                DomainError.ID_ATENDENTE_INVALIDO));

        if (!funcionario.podeBaixarPagamento()) {
            throw new DomainException(
                    DomainError.FUNCIONARIO_SEM_PERMISSAO);
        }

        OrdemServico ordemServico = ordemServicoRepository
                .buscarPorId(idOrdemServico)
                .orElseThrow(() ->
                        new DomainException(
                                DomainError.OS_NAO_ENCONTRADA));

        StatusOrdemServico statusAnterior = ordemServico.getStatus();

        ordemServico.entregarVeiculo();

        ordemServicoRepository.salvar(ordemServico);

        var historico = HistoricoStatusOrdemServico.registrar(
                idOrdemServico,
                statusAnterior,
                ordemServico.getStatus());

        historicoRepository.salvar(historico);
    }

    private UUID validarUuid(String valor, DomainError erro) {
        try {
            if (valor == null || valor.isBlank()) {
                throw new IllegalArgumentException();
            }

            return UUID.fromString(valor);
        } catch (IllegalArgumentException exception) {
            throw new DomainException(erro);
        }
    }
}