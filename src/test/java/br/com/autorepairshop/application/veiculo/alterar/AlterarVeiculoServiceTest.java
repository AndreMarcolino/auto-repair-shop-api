package br.com.autorepairshop.application.veiculo.alterar;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.Placa;
import br.com.autorepairshop.domain.veiculo.Veiculo;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlterarVeiculoServiceTest {

    private static final UUID VEICULO_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    private static final String PLACA_ATUAL = "ABC1D23";
    private static final String PLACA_NOVA = "XYZ9B87";

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private Veiculo veiculo;

    @InjectMocks
    private AlterarVeiculoService service;

    @Test
    @DisplayName("Deve alterar os dados do veículo quando a nova placa estiver disponível")
    void deveAlterarVeiculoQuandoPlacaDisponivel() {
        AlterarVeiculoInput input = criarInput(VEICULO_ID.toString(), PLACA_NOVA, "Toyota", "Corolla", 2022);

        Placa placaAtual = Placa.criar(PLACA_ATUAL);

        when(veiculoRepository.buscarPorId(VEICULO_ID)).thenReturn(Optional.of(veiculo));
        when(veiculo.getPlaca()).thenReturn(placaAtual);
        when(veiculoRepository.existePlaca(any(Placa.class))).thenReturn(false);

        service.executar(input);

        verify(veiculoRepository).buscarPorId(VEICULO_ID);
        verify(veiculoRepository).existePlaca(any(Placa.class));
        verify(veiculo).atualizarCadastro(any(Placa.class), eq("Toyota"), eq("Corolla"), eq(2022));
        verify(veiculoRepository).salvar(veiculo);
    }

    @Test
    @DisplayName("Deve atualizar sem consultar duplicidade quando a placa não mudar")
    void deveAtualizarSemConsultarDuplicidadeQuandoPlacaNaoMudar() {
        AlterarVeiculoInput input = criarInput(VEICULO_ID.toString(), PLACA_ATUAL, "Toyota", "Corolla", 2022);

        when(veiculoRepository.buscarPorId(VEICULO_ID)).thenReturn(Optional.of(veiculo));

        Placa placaAtual = Placa.criar(PLACA_ATUAL);

        try (MockedStatic<Placa> placaStatic = mockStatic(Placa.class)) {
            placaStatic.when(() -> Placa.criar(PLACA_ATUAL)).thenReturn(placaAtual);
            when(veiculo.getPlaca()).thenReturn(placaAtual);

            service.executar(input);

            verify(veiculoRepository, never()).existePlaca(any(Placa.class));
            verify(veiculo).atualizarCadastro(placaAtual, "Toyota", "Corolla", 2022);
            verify(veiculoRepository).salvar(veiculo);
        }
    }

    @Test
    @DisplayName("Deve rejeitar ID nulo ou inválido")
    void deveRejeitarIdInvalido() {
        assertThrows(DomainException.class, () -> service.executar(criarInput("id-invalido", PLACA_NOVA, "Toyota", "Corolla", 2022)));

        assertThrows(DomainException.class, () -> service.executar(criarInput(null, PLACA_NOVA, "Toyota", "Corolla", 2022)));

        verifyNoInteractions(veiculoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar veículo não encontrado")
    void deveRejeitarVeiculoNaoEncontrado() {
        when(veiculoRepository.buscarPorId(VEICULO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(criarInput(VEICULO_ID.toString(), PLACA_NOVA, "Toyota", "Corolla", 2022)));

        verify(veiculoRepository).buscarPorId(VEICULO_ID);
        verify(veiculoRepository, never()).salvar(any());
        verify(veiculoRepository, never()).existePlaca(any(Placa.class));
    }

    @Test
    @DisplayName("Deve rejeitar placa já cadastrada em outro veículo")
    void deveRejeitarPlacaDuplicada() {
        AlterarVeiculoInput input = criarInput(VEICULO_ID.toString(), PLACA_NOVA, "Toyota", "Corolla", 2022);

        Placa placaAtual = Placa.criar(PLACA_ATUAL);

        when(veiculoRepository.buscarPorId(VEICULO_ID)).thenReturn(Optional.of(veiculo));
        when(veiculo.getPlaca()).thenReturn(placaAtual);
        when(veiculoRepository.existePlaca(any(Placa.class))).thenReturn(true);

        assertThrows(DomainException.class, () -> service.executar(input));

        verify(veiculoRepository).existePlaca(any(Placa.class));
        verify(veiculo, never()).atualizarCadastro(any(Placa.class), anyString(), anyString(), anyInt());
        verify(veiculoRepository, never()).salvar(any());
    }

    private AlterarVeiculoInput criarInput(String id, String placa, String marca, String modelo, Integer anoFabricacao) {

        return new AlterarVeiculoInput(id, placa, marca, modelo, anoFabricacao);
    }
}