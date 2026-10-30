package br.com.autorepairshop.application.veiculo.criar;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.Placa;
import br.com.autorepairshop.domain.veiculo.Veiculo;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarVeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private CriarVeiculoService service;

    @Test
    @DisplayName("Deve criar e salvar o veículo quando a placa estiver disponível")
    void deveCriarESalvarVeiculo() {
        CriarVeiculoInput input = new CriarVeiculoInput("ABC1D23", "Toyota", "Corolla", 2022);

        UUID veiculoId = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

        when(veiculoRepository.existePlaca(any(Placa.class))).thenReturn(false);
        when(veiculoRepository.salvar(any(Veiculo.class))).thenAnswer(invocacao -> {
            Veiculo veiculo = invocacao.getArgument(0);
            // Pressupõe que Veiculo tenha um método para obter o ID
            // definido pelo domínio. Se getId() não existir ou não
            // puder ser definido, adapte o teste à implementação real.
            return veiculo;
        });

        CriarVeiculoOutput resultado = service.executar(input);

        ArgumentCaptor<Placa> placaCaptor = ArgumentCaptor.forClass(Placa.class);
        verify(veiculoRepository).existePlaca(placaCaptor.capture());

        ArgumentCaptor<Veiculo> veiculoCaptor = ArgumentCaptor.forClass(Veiculo.class);
        verify(veiculoRepository).salvar(veiculoCaptor.capture());

        Veiculo veiculoSalvo = veiculoCaptor.getValue();

        assertNotNull(placaCaptor.getValue());
        assertNotNull(veiculoSalvo);
        assertEquals("Toyota", veiculoSalvo.getMarca());
        assertEquals("Corolla", veiculoSalvo.getModelo());
        assertEquals(2022, veiculoSalvo.getAnoFabricacao());

        // O ID retornado depende do objeto devolvido pelo repositório.
        assertEquals(veiculoSalvo.getId(), resultado.id());

        verifyNoMoreInteractions(veiculoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar uma placa já cadastrada")
    void deveRejeitarPlacaDuplicada() {
        CriarVeiculoInput input = new CriarVeiculoInput("ABC1D23", "Toyota", "Corolla", 2022);

        when(veiculoRepository.existePlaca(any(Placa.class))).thenReturn(true);

        assertThrows(DomainException.class, () -> service.executar(input));

        verify(veiculoRepository).existePlaca(any(Placa.class));
        verify(veiculoRepository, never()).salvar(any());
        verifyNoMoreInteractions(veiculoRepository);
    }
}