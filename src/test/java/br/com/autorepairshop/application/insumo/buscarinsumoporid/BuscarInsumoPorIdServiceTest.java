package br.com.autorepairshop.application.insumo.buscarinsumoporid;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.application.insumo.shared.InsumoOutput;
import br.com.autorepairshop.domain.insumo.Insumo;
import br.com.autorepairshop.domain.insumo.InsumoRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarInsumoPorIdServiceTest {

    private static final UUID ID =
            UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private Insumo insumo;

    @Mock
    private InsumoOutput insumoOutput;

    @InjectMocks
    private BuscarInsumoPorIdService service;

    @Test
    @DisplayName("Deve retornar o insumo convertido quando encontrado")
    void deveRetornarInsumoQuandoEncontrado() {
        when(insumoRepository.buscarPorId(ID))
                .thenReturn(Optional.of(insumo));

        try (MockedStatic<InsumoOutput> conversao =
                     mockStatic(InsumoOutput.class)) {

            conversao.when(() -> InsumoOutput.fromDomain(insumo))
                    .thenReturn(insumoOutput);

            Optional<InsumoOutput> resultado =
                    service.executar(ID.toString());

            assertTrue(resultado.isPresent());
            assertSame(insumoOutput, resultado.orElseThrow());

            verify(insumoRepository).buscarPorId(ID);
            conversao.verify(() -> InsumoOutput.fromDomain(insumo));
        }
    }

    @Test
    @DisplayName("Deve retornar vazio quando o insumo não for encontrado")
    void deveRetornarVazioQuandoInsumoNaoEncontrado() {
        when(insumoRepository.buscarPorId(ID))
                .thenReturn(Optional.empty());

        try (MockedStatic<InsumoOutput> conversao =
                     mockStatic(InsumoOutput.class)) {

            Optional<InsumoOutput> resultado =
                    service.executar(ID.toString());

            assertTrue(resultado.isEmpty());

            verify(insumoRepository).buscarPorId(ID);
            conversao.verifyNoInteractions();
        }
    }

    @Test
    @DisplayName("Deve rejeitar ID inválido sem consultar o repositório")
    void deveRejeitarIdInvalido() {
        assertThrows(
                DomainException.class,
                () -> service.executar("id-invalido")
        );

        verifyNoInteractions(insumoRepository, insumo);
    }

    @Test
    @DisplayName("Deve rejeitar ID vazio sem consultar o repositório")
    void deveRejeitarIdVazio() {
        assertThrows(
                DomainException.class,
                () -> service.executar("")
        );

        verifyNoInteractions(insumoRepository, insumo);
    }

    @Test
    @DisplayName("Deve rejeitar ID nulo sem consultar o repositório")
    void deveRejeitarIdNulo() {
        assertThrows(
                DomainException.class,
                () -> service.executar(null)
        );

        verifyNoInteractions(insumoRepository, insumo);
    }
}