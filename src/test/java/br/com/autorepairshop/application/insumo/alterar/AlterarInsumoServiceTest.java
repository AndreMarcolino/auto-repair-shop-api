package br.com.autorepairshop.application.insumo.alterar;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.insumo.Insumo;
import br.com.autorepairshop.domain.insumo.InsumoRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlterarInsumoServiceTest {

    private static final UUID ID =
            UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    private static final String DESCRICAO = "Óleo de motor";
    private static final BigDecimal VALOR_REFERENCIA =
            new BigDecimal("39.90");

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private Insumo insumo;

    @InjectMocks
    private AlterarInsumoService service;

    @Test
    @DisplayName("Deve atualizar os dados do insumo e salvá-lo")
    void deveAlterarInsumoComSucesso() {
        AlterarInsumoInput input = criarInput(ID.toString());

        when(insumoRepository.buscarPorId(ID))
                .thenReturn(Optional.of(insumo));

        service.executar(input);

        InOrder ordem = inOrder(insumoRepository, insumo);

        ordem.verify(insumoRepository).buscarPorId(ID);
        ordem.verify(insumo).atualizarDados(
                DESCRICAO, VALOR_REFERENCIA);
        ordem.verify(insumoRepository).salvar(insumo);
    }

    @Test
    @DisplayName("Deve rejeitar insumo não encontrado sem salvar")
    void deveRejeitarInsumoNaoEncontrado() {
        AlterarInsumoInput input = criarInput(ID.toString());

        when(insumoRepository.buscarPorId(ID))
                .thenReturn(Optional.empty());

        assertThrows(
                DomainException.class,
                () -> service.executar(input)
        );

        verify(insumoRepository).buscarPorId(ID);
        verify(insumoRepository, never()).salvar(any());
        verifyNoInteractions(insumo);
    }

    @Test
    @DisplayName("Deve rejeitar ID inválido sem acessar o repositório")
    void deveRejeitarIdInvalido() {
        AlterarInsumoInput input = criarInput("id-invalido");

        assertThrows(
                DomainException.class,
                () -> service.executar(input)
        );

        verifyNoInteractions(insumoRepository, insumo);
    }

    @Test
    @DisplayName("Deve rejeitar ID nulo sem acessar o repositório")
    void deveRejeitarIdNulo() {
        AlterarInsumoInput input = criarInput(null);

        assertThrows(
                DomainException.class,
                () -> service.executar(input)
        );

        verifyNoInteractions(insumoRepository, insumo);
    }

    @Test
    @DisplayName("Não deve salvar se a atualização do insumo falhar")
    void naoDeveSalvarQuandoAtualizacaoFalhar() {
        AlterarInsumoInput input = criarInput(ID.toString());

        when(insumoRepository.buscarPorId(ID))
                .thenReturn(Optional.of(insumo));

        doThrow(new IllegalStateException("Falha ao atualizar"))
                .when(insumo)
                .atualizarDados(DESCRICAO, VALOR_REFERENCIA);

        assertThrows(
                IllegalStateException.class,
                () -> service.executar(input)
        );

        verify(insumo).atualizarDados(DESCRICAO, VALOR_REFERENCIA);
        verify(insumoRepository, never()).salvar(any());
    }

    private AlterarInsumoInput criarInput(String id) {
        return new AlterarInsumoInput(
                id,
                DESCRICAO,
                VALOR_REFERENCIA,
                15
        );
    }
}