package br.com.autorepairshop.application.veiculo.buscarveiculoporid;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.application.veiculo.shared.VeiculoOutput;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.Placa;
import br.com.autorepairshop.domain.veiculo.Veiculo;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarVeiculoPorIdServiceTest {

    private static final UUID VEICULO_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private Veiculo veiculo;

    @Test
    @DisplayName("Deve retornar o veículo quando encontrado")
    void deveRetornarVeiculoQuandoEncontrado() {
        when(veiculoRepository.buscarPorId(VEICULO_ID)).thenReturn(Optional.of(veiculo));
        when(veiculo.getPlaca()).thenReturn(Placa.criar("ABC1D23"));

        BuscarVeiculoPorIdService service = new BuscarVeiculoPorIdService(veiculoRepository);

        Optional<VeiculoOutput> resultado = service.executar(VEICULO_ID.toString());

        assertEquals(Optional.of(VeiculoOutput.fromDomain(veiculo)), resultado);

        verify(veiculoRepository).buscarPorId(VEICULO_ID);
        verifyNoMoreInteractions(veiculoRepository);
    }

    @Test
    @DisplayName("Deve retornar vazio quando o veículo não for encontrado")
    void deveRetornarVazioQuandoVeiculoNaoEncontrado() {
        when(veiculoRepository.buscarPorId(VEICULO_ID)).thenReturn(Optional.empty());

        BuscarVeiculoPorIdService service = new BuscarVeiculoPorIdService(veiculoRepository);

        Optional<VeiculoOutput> resultado = service.executar(VEICULO_ID.toString());

        assertEquals(Optional.empty(), resultado);

        verify(veiculoRepository).buscarPorId(VEICULO_ID);
        verifyNoMoreInteractions(veiculoRepository);
        verifyNoInteractions(veiculo);
    }

    @Test
    @DisplayName("Deve rejeitar ID nulo")
    void deveRejeitarIdNulo() {
        BuscarVeiculoPorIdService service = new BuscarVeiculoPorIdService(veiculoRepository);

        assertThrows(DomainException.class, () -> service.executar(null));

        verifyNoInteractions(veiculoRepository, veiculo);
    }

    @Test
    @DisplayName("Deve rejeitar ID inválido")
    void deveRejeitarIdInvalido() {
        BuscarVeiculoPorIdService service = new BuscarVeiculoPorIdService(veiculoRepository);

        assertThrows(DomainException.class, () -> service.executar("id-invalido"));

        verifyNoInteractions(veiculoRepository, veiculo);
    }
}