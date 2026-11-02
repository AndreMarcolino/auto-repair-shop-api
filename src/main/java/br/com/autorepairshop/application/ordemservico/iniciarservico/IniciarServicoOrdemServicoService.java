package br.com.autorepairshop.application.ordemservico.iniciarservico;

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
public class IniciarServicoOrdemServicoService
        implements IniciarServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final HistoricoStatusOrdemServicoRepository historicoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public IniciarServicoOrdemServicoService(
            OrdemServicoRepository ordemServicoRepository,
            HistoricoStatusOrdemServicoRepository historicoRepository,
            FuncionarioRepository funcionarioRepository) {

        this.ordemServicoRepository = ordemServicoRepository;
        this.historicoRepository = historicoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    @Transactional
    public void executar(IniciarServicoOrdemServicoInput input) {

        UUID idOrdemServico = validarUuid(
                input.idOrdemServico(),
                DomainError.ID_ORDEM_SERVICO_INVALIDO);

        UUID idMecanico = validarUuid(
                input.mecanicoId(),
                DomainError.ID_MECANICO_INVALIDO);

        Funcionario funcionario = funcionarioRepository
                .buscarPorId(idMecanico)
                .orElseThrow(() ->
                        new DomainException(DomainError.ID_MECANICO_INVALIDO));

        if (!funcionario.podeAtenderExecutarServico()) {
            throw new DomainException(DomainError.FUNCIONARIO_SEM_PERMISSAO);
        }

        OrdemServico ordemServico = ordemServicoRepository
                .buscarPorId(idOrdemServico)
                .orElseThrow(() ->
                        new DomainException(DomainError.OS_NAO_ENCONTRADA));

        StatusOrdemServico statusAnterior = ordemServico.getStatus();

        ordemServico.iniciarServico();

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