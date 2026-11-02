package br.com.autorepairshop.application.cliente.desativarcliente;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesativarClienteServiceTest {

    private static final UUID ID =
            UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private Cliente cliente;

    @InjectMocks
    private DesativarClienteService service;

    @Test
    @DisplayName("Deve desativar cliente antes de salvar")
    void deveDesativarClienteComSucesso() {
        when(clienteRepository.buscarPorId(ID))
                .thenReturn(Optional.of(cliente));

        service.executar(ID.toString());

        InOrder ordem = inOrder(clienteRepository, cliente);

        ordem.verify(clienteRepository).buscarPorId(ID);
        ordem.verify(cliente).desativar();
        ordem.verify(clienteRepository).salvar(cliente);
    }

    @Test
    @DisplayName("Deve rejeitar cliente não encontrado sem salvar")
    void deveRejeitarClienteNaoEncontrado() {
        when(clienteRepository.buscarPorId(ID))
                .thenReturn(Optional.empty());

        assertThrows(
                DomainException.class,
                () -> service.executar(ID.toString())
        );

        verify(clienteRepository).buscarPorId(ID);
        verify(clienteRepository, never()).salvar(any());
        verifyNoInteractions(cliente);
    }

    @Test
    @DisplayName("Deve rejeitar ID inválido sem acessar repositório")
    void deveRejeitarIdInvalido() {
        assertThrows(
                DomainException.class,
                () -> service.executar("id-invalido")
        );

        verifyNoInteractions(clienteRepository, cliente);
    }

    @Test
    @DisplayName("Deve rejeitar ID vazio sem acessar repositório")
    void deveRejeitarIdVazio() {
        assertThrows(
                DomainException.class,
                () -> service.executar("")
        );

        verifyNoInteractions(clienteRepository, cliente);
    }

    @Test
    @DisplayName("Deve rejeitar ID nulo sem acessar repositório")
    void deveRejeitarIdNulo() {
        assertThrows(
                DomainException.class,
                () -> service.executar(null)
        );

        verifyNoInteractions(clienteRepository, cliente);
    }

    @Test
    @DisplayName("Não deve salvar quando a desativação lançar uma exceção")
    void naoDeveSalvarQuandoDesativacaoFalhar() {
        when(clienteRepository.buscarPorId(ID))
                .thenReturn(Optional.of(cliente));

        doThrow(new IllegalStateException("Falha na desativação"))
                .when(cliente).desativar();

        assertThrows(
                IllegalStateException.class,
                () -> service.executar(ID.toString())
        );

        verify(cliente).desativar();
        verify(clienteRepository, never()).salvar(any());
    }
}