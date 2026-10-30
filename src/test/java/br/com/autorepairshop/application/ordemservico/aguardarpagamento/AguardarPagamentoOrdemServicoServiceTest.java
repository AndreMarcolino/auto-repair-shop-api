package br.com.autorepairshop.application.ordemservico.aguardarpagamento;

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

import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AguardarPagamentoOrdemServicoServiceTest {

    private static final UUID ORDEM_SERVICO_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private HistoricoStatusOrdemServicoRepository historicoRepository;

    @Mock
    private OrdemServico ordemServico;

    @Mock
    private HistoricoStatusOrdemServico historico;

    @InjectMocks
    private AguardarPagamentoOrdemServicoService service;

    @Test
    @DisplayName("Deve alterar o status, salvar a OS e registrar o histórico")
    void deveAguardarPagamentoESalvarHistorico() {
        when(ordemServicoRepository.buscarPorId(ORDEM_SERVICO_ID)).thenReturn(Optional.of(ordemServico));

        try (MockedStatic<HistoricoStatusOrdemServico> historicoStatic = mockStatic(HistoricoStatusOrdemServico.class)) {

            historicoStatic.when(() -> HistoricoStatusOrdemServico.registrar(ORDEM_SERVICO_ID, null, null)).thenReturn(historico);

            service.executar(ORDEM_SERVICO_ID);

            InOrder ordem = inOrder(ordemServicoRepository, ordemServico, historicoRepository);

            ordem.verify(ordemServicoRepository).buscarPorId(ORDEM_SERVICO_ID);
            ordem.verify(ordemServico).aguardarPagamento();
            ordem.verify(ordemServicoRepository).salvar(ordemServico);
            ordem.verify(historicoRepository).salvar(historico);

            historicoStatic.verify(() -> HistoricoStatusOrdemServico.registrar(ORDEM_SERVICO_ID, null, null));
        }
    }

    @Test
    @DisplayName("Deve rejeitar ID nulo sem consultar os repositórios")
    void deveRejeitarIdNulo() {
        assertThrows(DomainException.class, () -> service.executar(null));

        verifyNoInteractions(ordemServicoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ordem de serviço não encontrada")
    void deveRejeitarOrdemServicoNaoEncontrada() {
        when(ordemServicoRepository.buscarPorId(ORDEM_SERVICO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(ORDEM_SERVICO_ID));

        verify(ordemServicoRepository).buscarPorId(ORDEM_SERVICO_ID);
        verify(ordemServicoRepository, never()).salvar(any());
        verifyNoInteractions(historicoRepository);
    }
}