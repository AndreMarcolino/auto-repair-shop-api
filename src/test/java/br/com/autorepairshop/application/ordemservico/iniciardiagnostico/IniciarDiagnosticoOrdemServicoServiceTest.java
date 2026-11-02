package br.com.autorepairshop.application.ordemservico.iniciardiagnostico;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.funcionario.Funcionario;
import br.com.autorepairshop.domain.funcionario.FuncionarioRepository;
import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IniciarDiagnosticoOrdemServicoServiceTest {

    private static final UUID ORDEM_SERVICO_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    private static final UUID MECANICO_ID = UUID.fromString("b92e5ed1-a7ca-4b1d-8398-0e5d5c440029");

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private HistoricoStatusOrdemServicoRepository historicoRepository;

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private Funcionario funcionario;

    @Mock
    private OrdemServico ordemServico;

    @Mock
    private Orcamento orcamento;

    @Mock
    private HistoricoStatusOrdemServico historico;

    @InjectMocks
    private IniciarDiagnosticoOrdemServicoService service;

    @Test
    @DisplayName("Deve iniciar diagnóstico, criar orçamento e registrar histórico")
    void deveIniciarDiagnosticoCriarOrcamentoERegistrarHistorico() {
        IniciarDiagnosticoOrdemServicoInput input = criarInput(ORDEM_SERVICO_ID.toString(), MECANICO_ID.toString());

        when(funcionarioRepository.buscarPorId(MECANICO_ID)).thenReturn(Optional.of(funcionario));
        when(funcionario.podeAtenderExecutarServico()).thenReturn(true);
        when(funcionario.getId()).thenReturn(MECANICO_ID);
        when(ordemServicoRepository.buscarPorId(ORDEM_SERVICO_ID)).thenReturn(Optional.of(ordemServico));
        when(ordemServico.getId()).thenReturn(ORDEM_SERVICO_ID);

        try (MockedStatic<Orcamento> orcamentoStatic = mockStatic(Orcamento.class); MockedStatic<HistoricoStatusOrdemServico> historicoStatic = mockStatic(HistoricoStatusOrdemServico.class)) {

            orcamentoStatic.when(() -> Orcamento.criar(ORDEM_SERVICO_ID, MECANICO_ID)).thenReturn(orcamento);

            historicoStatic.when(() -> HistoricoStatusOrdemServico.registrar(ORDEM_SERVICO_ID, null, null)).thenReturn(historico);

            service.executar(input);

            InOrder ordem = inOrder(funcionarioRepository, ordemServicoRepository, ordemServico, orcamentoRepository, historicoRepository);

            ordem.verify(funcionarioRepository).buscarPorId(MECANICO_ID);
            ordem.verify(ordemServicoRepository).buscarPorId(ORDEM_SERVICO_ID);
            ordem.verify(ordemServico).iniciarDiagnostico();
            ordem.verify(ordemServicoRepository).salvar(ordemServico);
            ordem.verify(orcamentoRepository).salvar(orcamento);
            ordem.verify(historicoRepository).salvar(historico);

            orcamentoStatic.verify(() -> Orcamento.criar(ORDEM_SERVICO_ID, MECANICO_ID));

            historicoStatic.verify(() -> HistoricoStatusOrdemServico.registrar(ORDEM_SERVICO_ID, null, null));
        }
    }

    @Test
    @DisplayName("Deve rejeitar ID da ordem de serviço nulo")
    void deveRejeitarIdOrdemServicoNulo() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(null, MECANICO_ID.toString())));

        verifyNoInteractions(funcionarioRepository, ordemServicoRepository, orcamentoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ID da ordem de serviço em branco")
    void deveRejeitarIdOrdemServicoEmBranco() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(" ", MECANICO_ID.toString())));

        verifyNoInteractions(funcionarioRepository, ordemServicoRepository, orcamentoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ID da ordem de serviço inválido")
    void deveRejeitarIdOrdemServicoInvalido() {
        assertThrows(DomainException.class, () -> service.executar(criarInput("ordem-invalida", MECANICO_ID.toString())));

        verifyNoInteractions(funcionarioRepository, ordemServicoRepository, orcamentoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar mecânico não encontrado")
    void deveRejeitarMecanicoNaoEncontrado() {
        when(funcionarioRepository.buscarPorId(MECANICO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), MECANICO_ID.toString())));

        verify(funcionarioRepository).buscarPorId(MECANICO_ID);
        verifyNoInteractions(ordemServicoRepository, orcamentoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar mecânico sem permissão")
    void deveRejeitarMecanicoSemPermissao() {
        when(funcionarioRepository.buscarPorId(MECANICO_ID)).thenReturn(Optional.of(funcionario));
        when(funcionario.podeAtenderExecutarServico()).thenReturn(false);

        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), MECANICO_ID.toString())));

        verifyNoInteractions(ordemServicoRepository, orcamentoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ordem de serviço não encontrada")
    void deveRejeitarOrdemServicoNaoEncontrada() {
        when(funcionarioRepository.buscarPorId(MECANICO_ID)).thenReturn(Optional.of(funcionario));
        when(funcionario.podeAtenderExecutarServico()).thenReturn(true);

        when(ordemServicoRepository.buscarPorId(ORDEM_SERVICO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), MECANICO_ID.toString())));

        verify(ordemServicoRepository).buscarPorId(ORDEM_SERVICO_ID);
        verify(ordemServicoRepository, never()).salvar(any());
        verifyNoInteractions(orcamentoRepository, historicoRepository);
    }

    private IniciarDiagnosticoOrdemServicoInput criarInput(String idOrdemServico, String mecanicoId) {

        return new IniciarDiagnosticoOrdemServicoInput(idOrdemServico, mecanicoId);
    }
}