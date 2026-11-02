package br.com.autorepairshop.application.insumo.excluir;

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
class ExcluirInsumoServiceTest {

    private static final UUID ID =
            UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private Insumo insumo;

    @InjectMocks
    private ExcluirInsumoService service;

    @Test
    @DisplayName("Deve excluir o insumo encontrado")
    void deveExcluirInsumoEncontrado() {
        when(insumoRepository.buscarPorId(ID))
                .thenReturn(Optional.of(insumo));

        service.executar(ID.toString());

        InOrder ordem = inOrder(insumoRepository);
        ordem.verify(insumoRepository).buscarPorId(ID);
        ordem.verify(insumoRepository).excluir(ID);
    }

    @Test
    @DisplayName("Deve rejeitar insumo não encontrado sem excluí-lo")
    void deveRejeitarInsumoNaoEncontrado() {
        when(insumoRepository.buscarPorId(ID))
                .thenReturn(Optional.empty());

        assertThrows(
                DomainException.class,
                () -> service.executar(ID.toString())
        );

        verify(insumoRepository).buscarPorId(ID);
        verify(insumoRepository, never()).excluir(any(UUID.class));
    }

    @Test
    @DisplayName("Deve rejeitar ID inválido sem consultar o repositório")
    void deveRejeitarIdInvalido() {
        assertThrows(
                DomainException.class,
                () -> service.executar("id-invalido")
        );

        verifyNoInteractions(insumoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ID nulo sem consultar o repositório")
    void deveRejeitarIdNulo() {
        assertThrows(
                DomainException.class,
                () -> service.executar(null)
        );

        verifyNoInteractions(insumoRepository);
    }
}