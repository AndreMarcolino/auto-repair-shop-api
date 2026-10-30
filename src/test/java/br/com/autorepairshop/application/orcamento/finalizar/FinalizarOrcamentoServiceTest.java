package br.com.autorepairshop.application.orcamento.finalizar;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinalizarOrcamentoServiceTest {

    private static final UUID ORDEM_SERVICO_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    private static final String DIAGNOSTICO = "Diagnóstico detalhado do veículo";

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private HistoricoStatusOrdemServicoRepository historicoRepository;

    @Mock
    private Orcamento orcamento;

    @Mock
    private OrdemServico ordemServico;

    @Mock
    private HistoricoStatusOrdemServico historico;

    @InjectMocks
    private FinalizarOrcamentoService service;

    @Test
    @DisplayName("Deve finalizar orçamento, enviar OS para aprovação e registrar histórico")
    void deveFinalizarOrcamentoEEnviarOrdemServicoParaAprovacao() {
        FinalizarOrcamentoInput input = criarInput(ORDEM_SERVICO_ID.toString(), DIAGNOSTICO);

        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID)).thenReturn(Optional.of(orcamento));
        when(ordemServicoRepository.buscarPorId(ORDEM_SERVICO_ID)).thenReturn(Optional.of(ordemServico));
        when(ordemServico.getId()).thenReturn(ORDEM_SERVICO_ID);

        try (MockedStatic<HistoricoStatusOrdemServico> historicoStatic = mockStatic(HistoricoStatusOrdemServico.class)) {

            historicoStatic.when(() -> HistoricoStatusOrdemServico.registrar(ORDEM_SERVICO_ID, null, null)).thenReturn(historico);

            service.executar(input);

            InOrder ordem = inOrder(orcamentoRepository, ordemServicoRepository, orcamento, ordemServico, historicoRepository);

            ordem.verify(orcamentoRepository).buscarPorOrdemServicoId(ORDEM_SERVICO_ID);
            ordem.verify(ordemServicoRepository).buscarPorId(ORDEM_SERVICO_ID);
            ordem.verify(orcamento).finalizarElaboracao(DIAGNOSTICO);
            ordem.verify(ordemServico).enviarParaAprovacaoCliente();
            ordem.verify(orcamentoRepository).salvar(orcamento);
            ordem.verify(ordemServicoRepository).salvar(ordemServico);
            ordem.verify(historicoRepository).salvar(historico);

            historicoStatic.verify(() -> HistoricoStatusOrdemServico.registrar(ORDEM_SERVICO_ID, null, null));
        }
    }

    @Test
    @DisplayName("Deve rejeitar input nulo sem consultar os repositórios")
    void deveRejeitarInputNulo() {
        assertThrows(DomainException.class, () -> service.executar(null));

        verifyNoInteractions(orcamentoRepository, ordemServicoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ID da ordem de serviço em branco")
    void deveRejeitarIdOrdemServicoEmBranco() {
        FinalizarOrcamentoInput input = criarInput("  ", DIAGNOSTICO);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(orcamentoRepository, ordemServicoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ID da ordem de serviço inválido")
    void deveRejeitarIdOrdemServicoInvalido() {
        FinalizarOrcamentoInput input = criarInput("id-invalido", DIAGNOSTICO);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(orcamentoRepository, ordemServicoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar diagnóstico nulo ou em branco")
    void deveRejeitarDiagnosticoNuloOuEmBranco() {
        FinalizarOrcamentoInput input = criarInput(ORDEM_SERVICO_ID.toString(), " ");

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(orcamentoRepository, ordemServicoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar quando o orçamento não for encontrado")
    void deveRejeitarOrcamentoNaoEncontrado() {
        FinalizarOrcamentoInput input = criarInput(ORDEM_SERVICO_ID.toString(), DIAGNOSTICO);

        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(input));

        verify(orcamentoRepository).buscarPorOrdemServicoId(ORDEM_SERVICO_ID);
        verifyNoInteractions(ordemServicoRepository, historicoRepository);
        verify(orcamentoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve rejeitar quando a ordem de serviço não for encontrada")
    void deveRejeitarOrdemServicoNaoEncontrada() {
        FinalizarOrcamentoInput input = criarInput(ORDEM_SERVICO_ID.toString(), DIAGNOSTICO);

        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID)).thenReturn(Optional.of(orcamento));
        when(ordemServicoRepository.buscarPorId(ORDEM_SERVICO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(input));

        verify(ordemServicoRepository).buscarPorId(ORDEM_SERVICO_ID);
        verify(orcamento, never()).finalizarElaboracao(any());
        verify(ordemServicoRepository, never()).salvar(any());
        verifyNoInteractions(historicoRepository);
    }

    private FinalizarOrcamentoInput criarInput(String ordemServicoId, String diagnostico) {

        return new FinalizarOrcamentoInput(ordemServicoId, diagnostico);
    }
}