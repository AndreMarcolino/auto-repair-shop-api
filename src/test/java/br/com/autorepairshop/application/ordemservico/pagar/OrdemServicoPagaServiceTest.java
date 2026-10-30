package br.com.autorepairshop.application.ordemservico.pagar;

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
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoPagaServiceTest {

    private static final UUID ORDEM_SERVICO_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    private static final UUID ATENDENTE_ID = UUID.fromString("b92e5ed1-a7ca-4b1d-8398-0e5d5c440029");

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private HistoricoStatusOrdemServicoRepository historicoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private Funcionario funcionario;

    @Mock
    private OrdemServico ordemServico;

    @Mock
    private HistoricoStatusOrdemServico historico;

    @InjectMocks
    private OrdemServicoPagaService service;

    @Test
    @DisplayName("Deve registrar o pagamento, salvar a OS e registrar o histórico")
    void deveRegistrarPagamentoESalvarHistorico() {
        OrdemServicoPagaInput input = criarInput(ORDEM_SERVICO_ID.toString(), ATENDENTE_ID.toString());

        when(funcionarioRepository.buscarPorId(ATENDENTE_ID)).thenReturn(Optional.of(funcionario));
        when(funcionario.podeBaixarPagamento()).thenReturn(true);
        when(ordemServicoRepository.buscarPorId(ORDEM_SERVICO_ID)).thenReturn(Optional.of(ordemServico));

        try (MockedStatic<HistoricoStatusOrdemServico> historicoStatic = mockStatic(HistoricoStatusOrdemServico.class)) {

            historicoStatic.when(() -> HistoricoStatusOrdemServico.registrar(ORDEM_SERVICO_ID, null, null)).thenReturn(historico);

            service.executar(input);

            InOrder ordem = inOrder(funcionarioRepository, ordemServicoRepository, ordemServico, historicoRepository);

            ordem.verify(funcionarioRepository).buscarPorId(ATENDENTE_ID);
            ordem.verify(ordemServicoRepository).buscarPorId(ORDEM_SERVICO_ID);
            ordem.verify(ordemServico).registrarPagamento();
            ordem.verify(ordemServicoRepository).salvar(ordemServico);
            ordem.verify(historicoRepository).salvar(historico);

            historicoStatic.verify(() -> HistoricoStatusOrdemServico.registrar(ORDEM_SERVICO_ID, null, null));
        }
    }

    @Test
    @DisplayName("Deve rejeitar ID da ordem de serviço nulo")
    void deveRejeitarIdOrdemServicoNulo() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(null, ATENDENTE_ID.toString())));

        verificarNenhumaInteracaoComRepositorios();
    }

    @Test
    @DisplayName("Deve rejeitar ID da ordem de serviço em branco")
    void deveRejeitarIdOrdemServicoEmBranco() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(" ", ATENDENTE_ID.toString())));

        verificarNenhumaInteracaoComRepositorios();
    }

    @Test
    @DisplayName("Deve rejeitar ID da ordem de serviço inválido")
    void deveRejeitarIdOrdemServicoInvalido() {
        assertThrows(DomainException.class, () -> service.executar(criarInput("os-invalida", ATENDENTE_ID.toString())));

        verificarNenhumaInteracaoComRepositorios();
    }

    @Test
    @DisplayName("Deve rejeitar ID do atendente nulo")
    void deveRejeitarIdAtendenteNulo() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), null)));

        verificarNenhumaInteracaoComRepositorios();
    }

    @Test
    @DisplayName("Deve rejeitar ID do atendente em branco")
    void deveRejeitarIdAtendenteEmBranco() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), " ")));

        verificarNenhumaInteracaoComRepositorios();
    }

    @Test
    @DisplayName("Deve rejeitar ID do atendente inválido")
    void deveRejeitarIdAtendenteInvalido() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), "atendente-invalido")));

        verificarNenhumaInteracaoComRepositorios();
    }

    @Test
    @DisplayName("Deve rejeitar atendente não encontrado")
    void deveRejeitarAtendenteNaoEncontrado() {
        when(funcionarioRepository.buscarPorId(ATENDENTE_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), ATENDENTE_ID.toString())));

        verify(funcionarioRepository).buscarPorId(ATENDENTE_ID);
        verifyNoInteractions(ordemServicoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar atendente sem permissão para baixar pagamento")
    void deveRejeitarAtendenteSemPermissao() {
        when(funcionarioRepository.buscarPorId(ATENDENTE_ID)).thenReturn(Optional.of(funcionario));
        when(funcionario.podeBaixarPagamento()).thenReturn(false);

        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), ATENDENTE_ID.toString())));

        verifyNoInteractions(ordemServicoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ordem de serviço não encontrada")
    void deveRejeitarOrdemServicoNaoEncontrada() {
        when(funcionarioRepository.buscarPorId(ATENDENTE_ID)).thenReturn(Optional.of(funcionario));
        when(funcionario.podeBaixarPagamento()).thenReturn(true);
        when(ordemServicoRepository.buscarPorId(ORDEM_SERVICO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), ATENDENTE_ID.toString())));

        verify(ordemServicoRepository).buscarPorId(ORDEM_SERVICO_ID);
        verify(ordemServicoRepository, never()).salvar(any());
        verifyNoInteractions(historicoRepository);
    }

    private void verificarNenhumaInteracaoComRepositorios() {
        verifyNoInteractions(funcionarioRepository, ordemServicoRepository, historicoRepository);
    }

    private OrdemServicoPagaInput criarInput(String idOrdemServico, String atendenteId) {

        return new OrdemServicoPagaInput(idOrdemServico, atendenteId);
    }
}