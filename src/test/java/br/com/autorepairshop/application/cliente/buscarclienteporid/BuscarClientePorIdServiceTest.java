package br.com.autorepairshop.application.cliente.buscarclienteporid;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.application.cliente.shared.ClienteOutput;
import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarClientePorIdServiceTest {

    private static final UUID ID =
            UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private Cliente cliente;

    @Mock
    private ClienteOutput clienteOutput;

    @InjectMocks
    private BuscarClientePorIdService service;

    @Test
    @DisplayName("Deve retornar cliente convertido quando o ID existir")
    void deveRetornarClienteQuandoEncontrado() {
        when(clienteRepository.buscarPorId(ID))
                .thenReturn(Optional.of(cliente));

        try (MockedStatic<ClienteOutput> conversao =
                     mockStatic(ClienteOutput.class)) {

            conversao.when(() -> ClienteOutput.fromDomain(cliente))
                    .thenReturn(clienteOutput);

            Optional<ClienteOutput> resultado =
                    service.executar(ID.toString());

            assertTrue(resultado.isPresent());
            assertSame(clienteOutput, resultado.orElseThrow());

            verify(clienteRepository).buscarPorId(ID);
            conversao.verify(() -> ClienteOutput.fromDomain(cliente));
        }
    }

    @Test
    @DisplayName("Deve retornar vazio quando o cliente não existir")
    void deveRetornarVazioQuandoClienteNaoEncontrado() {
        when(clienteRepository.buscarPorId(ID))
                .thenReturn(Optional.empty());

        try (MockedStatic<ClienteOutput> conversao =
                     mockStatic(ClienteOutput.class)) {

            Optional<ClienteOutput> resultado =
                    service.executar(ID.toString());

            assertTrue(resultado.isEmpty());

            verify(clienteRepository).buscarPorId(ID);
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

        verifyNoInteractions(clienteRepository, cliente);
    }

    @Test
    @DisplayName("Deve rejeitar ID vazio sem consultar o repositório")
    void deveRejeitarIdVazio() {
        assertThrows(
                DomainException.class,
                () -> service.executar("")
        );

        verifyNoInteractions(clienteRepository, cliente);
    }

    @Test
    @DisplayName("Deve rejeitar ID nulo sem consultar o repositório")
    void deveRejeitarIdNulo() {
        assertThrows(
                DomainException.class,
                () -> service.executar(null)
        );

        verifyNoInteractions(clienteRepository, cliente);
    }
}