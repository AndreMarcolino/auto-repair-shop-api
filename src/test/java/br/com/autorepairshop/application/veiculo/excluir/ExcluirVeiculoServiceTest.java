package br.com.autorepairshop.application.veiculo.excluir;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.veiculo.Veiculo;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcluirVeiculoServiceTest {

    private static final UUID VEICULO_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private Veiculo veiculo;

    @InjectMocks
    private ExcluirVeiculoService service;

    @Test
    @DisplayName("Deve excluir o veículo encontrado")
    void deveExcluirVeiculoEncontrado() {
        when(veiculoRepository.buscarPorId(VEICULO_ID)).thenReturn(Optional.of(veiculo));

        service.executar(VEICULO_ID.toString());

        verify(veiculoRepository).buscarPorId(VEICULO_ID);
        verify(veiculoRepository).excluir(VEICULO_ID);
        verifyNoMoreInteractions(veiculoRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o ID for nulo")
    void deveRejeitarIdNulo() {
        assertThrows(DomainException.class, () -> service.executar(null));

        verifyNoInteractions(veiculoRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o ID for inválido")
    void deveRejeitarIdInvalido() {
        assertThrows(DomainException.class, () -> service.executar("id-invalido"));

        verifyNoInteractions(veiculoRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o veículo não for encontrado")
    void deveRejeitarVeiculoNaoEncontrado() {
        when(veiculoRepository.buscarPorId(VEICULO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(VEICULO_ID.toString()));

        verify(veiculoRepository).buscarPorId(VEICULO_ID);
        verify(veiculoRepository, never()).excluir(any(UUID.class));
        verifyNoMoreInteractions(veiculoRepository);
    }
}