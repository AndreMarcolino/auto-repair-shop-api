package br.com.autorepairshop.application.ordemservico.liberaraguardandoinsumos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import br.com.autorepairshop.domain.estoque.Estoque;
import br.com.autorepairshop.domain.estoque.EstoqueRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoInsumoPendente;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoInsumoPendenteRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LiberarOrdensServicoAguardandoInsumosServiceTest {

    private static final UUID ORDEM_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    private static final UUID INSUMO_ID = UUID.fromString("b92e5ed1-a7ca-4b1d-8398-0e5d5c440029");

    private static final BigDecimal QUANTIDADE = new BigDecimal("2");

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private OrdemServicoInsumoPendenteRepository pendenteRepository;

    @Mock
    private HistoricoStatusOrdemServicoRepository historicoRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private TransactionStatus transactionStatus;

    @Mock
    private OrdemServico ordemServico;

    @Mock
    private OrdemServicoInsumoPendente pendencia;

    @Mock
    private Estoque estoque;

    @Mock
    private HistoricoStatusOrdemServico historico;

    private LiberarOrdensServicoAguardandoInsumosService service;

    @BeforeEach
    void setUp() {
        service = new LiberarOrdensServicoAguardandoInsumosService(ordemServicoRepository, estoqueRepository, pendenteRepository, historicoRepository, transactionManager);

        when(transactionManager.getTransaction(any(TransactionDefinition.class))).thenReturn(transactionStatus);
    }

    @Test
    @DisplayName("Deve baixar o estoque, liberar a OS e registrar o histórico")
    void deveLiberarOrdemQuandoHaEstoqueDisponivel() {
        configurarOrdemComPendencia();
        when(pendencia.getInsumoId()).thenReturn(INSUMO_ID);
        when(pendencia.getQuantidadeNecessaria()).thenReturn(QUANTIDADE);
        when(estoqueRepository.buscarParaAtualizacao(INSUMO_ID)).thenReturn(Optional.of(estoque));
        when(estoque.possuiDisponibilidade(QUANTIDADE)).thenReturn(true);

        try (MockedStatic<HistoricoStatusOrdemServico> historicoStatic = mockStatic(HistoricoStatusOrdemServico.class)) {

            historicoStatic.when(() -> HistoricoStatusOrdemServico.registrar(ORDEM_ID, StatusOrdemServico.AGUARDANDO_INSUMOS, StatusOrdemServico.AGUARDANDO_INSUMOS)).thenReturn(historico);

            service.executar();

            InOrder ordem = inOrder(estoque, estoqueRepository, ordemServico, ordemServicoRepository, pendenteRepository, historicoRepository);

            ordem.verify(estoque).baixar(QUANTIDADE);
            ordem.verify(estoqueRepository).salvar(estoque);
            ordem.verify(ordemServico).liberarAposReposicaoDePecas();
            ordem.verify(ordemServicoRepository).salvar(ordemServico);
            ordem.verify(pendenteRepository).excluirPorOrdemServicoId(ORDEM_ID);
            ordem.verify(historicoRepository).salvar(historico);

            historicoStatic.verify(() -> HistoricoStatusOrdemServico.registrar(ORDEM_ID, StatusOrdemServico.AGUARDANDO_INSUMOS, StatusOrdemServico.AGUARDANDO_INSUMOS));

            verify(transactionManager).getTransaction(argThat(definition -> definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW));
            verify(transactionManager).commit(transactionStatus);
        }
    }

    @Test
    @DisplayName("Não deve processar a OS quando ela não for encontrada")
    void naoDeveProcessarOrdemNaoEncontrada() {
        when(ordemServicoRepository.buscarPorStatus(StatusOrdemServico.AGUARDANDO_INSUMOS)).thenReturn(List.of(ordemServico));
        when(ordemServico.getId()).thenReturn(ORDEM_ID);
        when(ordemServicoRepository.buscarPorId(ORDEM_ID)).thenReturn(Optional.empty());

        service.executar();

        verifyNoInteractions(estoqueRepository, pendenteRepository, historicoRepository);
        verify(ordemServicoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Não deve processar a OS se ela não estiver aguardando peças")
    void naoDeveProcessarOrdemComOutroStatus() {
        when(ordemServicoRepository.buscarPorStatus(StatusOrdemServico.AGUARDANDO_INSUMOS)).thenReturn(List.of(ordemServico));
        when(ordemServico.getId()).thenReturn(ORDEM_ID);
        when(ordemServicoRepository.buscarPorId(ORDEM_ID)).thenReturn(Optional.of(ordemServico));
        when(ordemServico.getStatus()).thenReturn(StatusOrdemServico.EM_EXECUCAO);

        service.executar();

        verifyNoInteractions(estoqueRepository, pendenteRepository, historicoRepository);
        verify(ordemServicoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Não deve liberar a OS se não houver pendências")
    void naoDeveLiberarOrdemSemPendencias() {
        when(ordemServicoRepository.buscarPorStatus(StatusOrdemServico.AGUARDANDO_INSUMOS)).thenReturn(List.of(ordemServico));
        when(ordemServico.getId()).thenReturn(ORDEM_ID);
        when(ordemServicoRepository.buscarPorId(ORDEM_ID)).thenReturn(Optional.of(ordemServico));
        when(ordemServico.getStatus()).thenReturn(StatusOrdemServico.AGUARDANDO_INSUMOS);
        when(pendenteRepository.buscarPorOrdemServicoId(ORDEM_ID)).thenReturn(List.of());

        service.executar();

        verifyNoInteractions(estoqueRepository, historicoRepository);
        verify(ordemServicoRepository, never()).salvar(any());
        verify(pendenteRepository, never()).excluirPorOrdemServicoId(any());
    }

    @Test
    @DisplayName("Não deve baixar estoque se o registro de estoque não existir")
    void naoDeveLiberarOrdemQuandoEstoqueNaoEncontrado() {
        configurarOrdemComPendencia();
        when(pendencia.getInsumoId()).thenReturn(INSUMO_ID);
        when(estoqueRepository.buscarParaAtualizacao(INSUMO_ID)).thenReturn(Optional.empty());

        service.executar();

        verify(estoqueRepository, never()).salvar(any());
        verify(ordemServico, never()).liberarAposReposicaoDePecas();
        verify(ordemServicoRepository, never()).salvar(any());
        verify(pendenteRepository, never()).excluirPorOrdemServicoId(any());
        verifyNoInteractions(historicoRepository);
    }

    @Test
    @DisplayName("Não deve liberar a OS quando o estoque for insuficiente")
    void naoDeveLiberarOrdemQuandoEstoqueInsuficiente() {
        configurarOrdemComPendencia();
        when(pendencia.getInsumoId()).thenReturn(INSUMO_ID);
        when(pendencia.getQuantidadeNecessaria()).thenReturn(QUANTIDADE);
        when(estoqueRepository.buscarParaAtualizacao(INSUMO_ID)).thenReturn(Optional.of(estoque));
        when(estoque.possuiDisponibilidade(QUANTIDADE)).thenReturn(false);

        service.executar();

        verify(estoque, never()).baixar(any());
        verify(estoqueRepository, never()).salvar(any());
        verify(ordemServico, never()).liberarAposReposicaoDePecas();
        verify(ordemServicoRepository, never()).salvar(any());
        verify(pendenteRepository, never()).excluirPorOrdemServicoId(any());
        verifyNoInteractions(historicoRepository);
    }

    private void configurarOrdemComPendencia() {
        when(ordemServicoRepository.buscarPorStatus(StatusOrdemServico.AGUARDANDO_INSUMOS)).thenReturn(List.of(ordemServico));
        when(ordemServico.getId()).thenReturn(ORDEM_ID);
        when(ordemServicoRepository.buscarPorId(ORDEM_ID)).thenReturn(Optional.of(ordemServico));
        when(ordemServico.getStatus()).thenReturn(StatusOrdemServico.AGUARDANDO_INSUMOS);
        when(pendenteRepository.buscarPorOrdemServicoId(ORDEM_ID)).thenReturn(List.of(pendencia));
    }
}