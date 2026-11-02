package br.com.autorepairshop.application.orcamento.adicionarservico;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.application.orcamento.shared.ServicoOrcamentoInput;
import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.servico.Servico;
import br.com.autorepairshop.domain.servico.ServicoRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdicionarServicoServiceTest {

    private static final UUID ORDEM_SERVICO_ID =
            UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");
    private static final UUID SERVICO_ID =
            UUID.fromString("b92e5ed1-a7ca-4b1d-8398-0e5d5c440029");

    private static final String UUID_USUARIO =
            "c92e5ed1-a7ca-4b1d-8398-0e5d5c440030";

    private static final String DESCRICAO_SERVICO = "Troca de óleo";
    private static final BigDecimal QUANTIDADE = new BigDecimal("1.000");
    private static final BigDecimal VALOR_REFERENCIA =
            new BigDecimal("120.00");

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private Servico servico;

    @Mock
    private Orcamento orcamento;

    @InjectMocks
    private AdicionarServicoService service;

    @Test
    @DisplayName("Deve adicionar o serviço ao orçamento e salvar")
    void deveAdicionarServicoESalvarOrcamento() {
        AdicionarServicoInput input = criarInput(
                ORDEM_SERVICO_ID.toString(),
                new ServicoOrcamentoInput(
                        SERVICO_ID.toString(),
                        QUANTIDADE
                )
        );

        when(servicoRepository.buscarPorIds(List.of(SERVICO_ID)))
                .thenReturn(List.of(servico));
        when(servico.getId()).thenReturn(SERVICO_ID);
        when(servico.getDescricao()).thenReturn(DESCRICAO_SERVICO);
        when(servico.getValorReferencia()).thenReturn(VALOR_REFERENCIA);
        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID))
                .thenReturn(Optional.of(orcamento));

        service.executar(input);

        InOrder ordem = inOrder(
                servicoRepository,
                orcamentoRepository,
                orcamento
        );

        ordem.verify(servicoRepository).buscarPorIds(List.of(SERVICO_ID));
        ordem.verify(orcamentoRepository)
                .buscarPorOrdemServicoId(ORDEM_SERVICO_ID);
        ordem.verify(orcamento).adicionarServico(
                SERVICO_ID,
                DESCRICAO_SERVICO,
                QUANTIDADE,
                VALOR_REFERENCIA
        );
        ordem.verify(orcamentoRepository).salvar(orcamento);
    }

    @Test
    @DisplayName("Deve rejeitar ordem de serviço nula sem consultar repositórios")
    void deveRejeitarOrdemServicoNula() {
        AdicionarServicoInput input = criarInput(
                null,
                new ServicoOrcamentoInput(
                        SERVICO_ID.toString(),
                        QUANTIDADE
                )
        );

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(servicoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar lista de serviços vazia")
    void deveRejeitarListaDeServicosVazia() {
        AdicionarServicoInput input = new AdicionarServicoInput(
                ORDEM_SERVICO_ID.toString(),
                List.of(),
                UUID_USUARIO
        );

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(servicoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ID de serviço em branco")
    void deveRejeitarIdServicoEmBranco() {
        AdicionarServicoInput input = criarInput(
                ORDEM_SERVICO_ID.toString(),
                new ServicoOrcamentoInput(" ", QUANTIDADE)
        );

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(servicoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar UUID inválido da ordem de serviço")
    void deveRejeitarUuidOrdemServicoInvalido() {
        AdicionarServicoInput input = criarInput(
                "ordem-invalida",
                new ServicoOrcamentoInput(
                        SERVICO_ID.toString(),
                        QUANTIDADE
                )
        );

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(servicoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar UUID inválido do serviço")
    void deveRejeitarUuidServicoInvalido() {
        AdicionarServicoInput input = criarInput(
                ORDEM_SERVICO_ID.toString(),
                new ServicoOrcamentoInput("servico-invalido", QUANTIDADE)
        );

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(servicoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar quando um serviço não for encontrado")
    void deveRejeitarServicoNaoEncontrado() {
        AdicionarServicoInput input = criarInput(
                ORDEM_SERVICO_ID.toString(),
                new ServicoOrcamentoInput(
                        SERVICO_ID.toString(),
                        QUANTIDADE
                )
        );

        when(servicoRepository.buscarPorIds(List.of(SERVICO_ID)))
                .thenReturn(List.of());

        assertThrows(DomainException.class, () -> service.executar(input));

        verify(servicoRepository).buscarPorIds(List.of(SERVICO_ID));
        verifyNoInteractions(orcamentoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar quando o orçamento não for encontrado")
    void deveRejeitarOrcamentoNaoEncontrado() {
        AdicionarServicoInput input = criarInput(
                ORDEM_SERVICO_ID.toString(),
                new ServicoOrcamentoInput(
                        SERVICO_ID.toString(),
                        QUANTIDADE
                )
        );

        when(servicoRepository.buscarPorIds(List.of(SERVICO_ID)))
                .thenReturn(List.of(servico));
        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID))
                .thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(input));

        verify(orcamentoRepository)
                .buscarPorOrdemServicoId(ORDEM_SERVICO_ID);
        verify(orcamentoRepository, never()).salvar(any());
        verify(orcamento, never()).adicionarServico(
                any(), any(), any(), any());
    }

    private AdicionarServicoInput criarInput(
            String ordemServicoId,
            ServicoOrcamentoInput servicoInput) {

        return new AdicionarServicoInput(
                ordemServicoId,
                List.of(servicoInput),
                UUID_USUARIO
        );
    }
}