package br.com.autorepairshop.application.servico.excluir;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.servico.Servico;
import br.com.autorepairshop.domain.servico.ServicoRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcluirServicoServiceTest {

    private static final UUID SERVICO_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private Servico servico;

    @InjectMocks
    private ExcluirServicoService service;

    @Test
    @DisplayName("Deve excluir serviço encontrado")
    void deveExcluirServicoEncontrado() {
        when(servicoRepository.buscarPorId(SERVICO_ID)).thenReturn(Optional.of(servico));

        service.executar(SERVICO_ID.toString());

        verify(servicoRepository).buscarPorId(SERVICO_ID);
        verify(servicoRepository).excluir(SERVICO_ID);
        verifyNoMoreInteractions(servicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ID nulo")
    void deveRejeitarIdNulo() {
        assertThrows(DomainException.class, () -> service.executar(null));

        verifyNoInteractions(servicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ID inválido")
    void deveRejeitarIdInvalido() {
        assertThrows(DomainException.class, () -> service.executar("id-invalido"));

        verifyNoInteractions(servicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar serviço não encontrado")
    void deveRejeitarServicoNaoEncontrado() {
        when(servicoRepository.buscarPorId(SERVICO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(SERVICO_ID.toString()));

        verify(servicoRepository).buscarPorId(SERVICO_ID);
        verify(servicoRepository, never()).excluir(any(UUID.class));
        verifyNoMoreInteractions(servicoRepository);
    }
}