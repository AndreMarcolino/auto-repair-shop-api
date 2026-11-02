package br.com.autorepairshop.application.orcamento.adicionarinsumo;

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

import br.com.autorepairshop.application.orcamento.shared.InsumoOrcamentoInput;
import br.com.autorepairshop.domain.insumo.Insumo;
import br.com.autorepairshop.domain.insumo.InsumoRepository;
import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdicionarInsumoServiceTest {

    private static final UUID ORDEM_SERVICO_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    private static final UUID INSUMO_ID = UUID.fromString("b92e5ed1-a7ca-4b1d-8398-0e5d5c440029");

    private static final String UUID_USUARIO = "c92e5ed1-a7ca-4b1d-8398-0e5d5c440030";

    private static final String DESCRICAO_INSUMO = "Óleo de motor";
    private static final BigDecimal QUANTIDADE = new BigDecimal("2.000");
    private static final BigDecimal VALOR_REFERENCIA = new BigDecimal("39.90");

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private Insumo insumo;

    @Mock
    private Orcamento orcamento;

    @InjectMocks
    private AdicionarInsumoService service;

    @Test
    @DisplayName("Deve adicionar o insumo ao orçamento e salvar")
    void deveAdicionarInsumoESalvarOrcamento() {
        AdicionarInsumoInput input = criarInput(ORDEM_SERVICO_ID.toString(), UUID_USUARIO, INSUMO_ID.toString(), QUANTIDADE);

        when(insumoRepository.buscarPorIds(List.of(INSUMO_ID))).thenReturn(List.of(insumo));
        when(insumo.getId()).thenReturn(INSUMO_ID);
        when(insumo.getDescricao()).thenReturn(DESCRICAO_INSUMO);
        when(insumo.getValorReferencia()).thenReturn(VALOR_REFERENCIA);
        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID)).thenReturn(Optional.of(orcamento));

        service.executar(input);

        InOrder ordem = inOrder(insumoRepository, orcamentoRepository, orcamento);

        ordem.verify(insumoRepository).buscarPorIds(List.of(INSUMO_ID));
        ordem.verify(orcamentoRepository).buscarPorOrdemServicoId(ORDEM_SERVICO_ID);
        ordem.verify(orcamento).adicionarInsumo(INSUMO_ID, DESCRICAO_INSUMO, QUANTIDADE, VALOR_REFERENCIA);
        ordem.verify(orcamentoRepository).salvar(orcamento);
    }

    @Test
    @DisplayName("Deve rejeitar ordem de serviço nula sem consultar os repositórios")
    void deveRejeitarOrdemServicoNula() {
        AdicionarInsumoInput input = criarInput(null, UUID_USUARIO, INSUMO_ID.toString(), QUANTIDADE);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(insumoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar lista de insumos vazia")
    void deveRejeitarListaDeInsumosVazia() {
        AdicionarInsumoInput input = new AdicionarInsumoInput(ORDEM_SERVICO_ID.toString(), List.of(), UUID_USUARIO);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(insumoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar usuário nulo")
    void deveRejeitarUsuarioNulo() {
        AdicionarInsumoInput input = criarInput(ORDEM_SERVICO_ID.toString(), null, INSUMO_ID.toString(), QUANTIDADE);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(insumoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ID de insumo em branco")
    void deveRejeitarIdDeInsumoEmBranco() {
        AdicionarInsumoInput input = criarInput(ORDEM_SERVICO_ID.toString(), UUID_USUARIO, " ", QUANTIDADE);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(insumoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar UUID inválido de ordem de serviço")
    void deveRejeitarUuidOrdemServicoInvalido() {
        AdicionarInsumoInput input = criarInput("ordem-invalida", UUID_USUARIO, INSUMO_ID.toString(), QUANTIDADE);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(insumoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar UUID inválido de insumo")
    void deveRejeitarUuidInsumoInvalido() {
        AdicionarInsumoInput input = criarInput(ORDEM_SERVICO_ID.toString(), UUID_USUARIO, "insumo-invalido", QUANTIDADE);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(insumoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar quando a quantidade de insumos encontrados divergir")
    void deveRejeitarQuandoInsumoNaoForEncontrado() {
        AdicionarInsumoInput input = criarInput(ORDEM_SERVICO_ID.toString(), UUID_USUARIO, INSUMO_ID.toString(), QUANTIDADE);

        when(insumoRepository.buscarPorIds(List.of(INSUMO_ID))).thenReturn(List.of());

        assertThrows(DomainException.class, () -> service.executar(input));

        verify(insumoRepository).buscarPorIds(List.of(INSUMO_ID));
        verifyNoInteractions(orcamentoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar quando o orçamento não for encontrado")
    void deveRejeitarOrcamentoNaoEncontrado() {
        AdicionarInsumoInput input = criarInput(ORDEM_SERVICO_ID.toString(), UUID_USUARIO, INSUMO_ID.toString(), QUANTIDADE);

        when(insumoRepository.buscarPorIds(List.of(INSUMO_ID))).thenReturn(List.of(insumo));
        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(input));

        verify(insumoRepository).buscarPorIds(List.of(INSUMO_ID));
        verify(orcamentoRepository).buscarPorOrdemServicoId(ORDEM_SERVICO_ID);
        verify(orcamentoRepository, never()).salvar(any());
        verify(orcamento, never()).adicionarInsumo(any(), any(), any(), any());
    }

    private AdicionarInsumoInput criarInput(String ordemServicoId, String uuidUsuario, String idInsumo, BigDecimal quantidade) {

        return new AdicionarInsumoInput(ordemServicoId, List.of(new InsumoOrcamentoInput(idInsumo, quantidade)), uuidUsuario);
    }
}