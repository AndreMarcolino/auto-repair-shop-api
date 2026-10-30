package br.com.autorepairshop.application.veiculo.listar;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.application.shared.dto.PaginacaoInput;
import br.com.autorepairshop.application.shared.dto.PaginacaoOutput;
import br.com.autorepairshop.application.veiculo.shared.VeiculoOutput;
import br.com.autorepairshop.domain.shared.Pagina;
import br.com.autorepairshop.domain.shared.valueobject.Placa;
import br.com.autorepairshop.domain.veiculo.Veiculo;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarVeiculosServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private Veiculo veiculo;

    @Test
    @DisplayName("Deve listar veículos aplicando o filtro de placa")
    void deveListarVeiculosComFiltroDePlaca() {
        ListarVeiculosInput input = new ListarVeiculosInput("ABC1D23", "Toyota", "Corolla", 2022);
        PaginacaoInput paginacao = new PaginacaoInput(0, 10);

        Pagina<Veiculo> pagina = new Pagina<>(List.of(veiculo), 0, 10, 1, 1);

        when(veiculoRepository.buscar(any(), eq(input.marca()), eq(input.modelo()), eq(input.anoFabricacao()), eq(paginacao.toPaginacao()))).thenReturn(pagina);

        when(veiculo.getPlaca()).thenReturn(Placa.criar("ABC1D23"));
        when(veiculo.getMarca()).thenReturn("Toyota");
        when(veiculo.getModelo()).thenReturn("Corolla");

        ListarVeiculosService service = new ListarVeiculosService(veiculoRepository);

        ListarVeiculosOutput resultado = service.executar(input, paginacao);

        String placaNormalizada = Placa.criar(input.placa()).placa();

        verify(veiculoRepository).buscar(placaNormalizada, input.marca(), input.modelo(), input.anoFabricacao(), paginacao.toPaginacao());

        assertEquals(List.of(VeiculoOutput.fromDomain(veiculo)), resultado.veiculos());
        assertEquals(PaginacaoOutput.de(pagina), resultado.paginacao());

        verifyNoMoreInteractions(veiculoRepository);
    }

    @Test
    @DisplayName("Deve listar veículos sem filtro de placa quando ela for nula")
    void deveListarVeiculosComPlacaNula() {
        ListarVeiculosInput input = new ListarVeiculosInput(null, "Toyota", "Corolla", 2022);
        PaginacaoInput paginacao = new PaginacaoInput(0, 10);

        Pagina<Veiculo> paginaVazia = new Pagina<>(List.of(), 0, 10, 0, 0);

        when(veiculoRepository.buscar(isNull(), eq(input.marca()), eq(input.modelo()), eq(input.anoFabricacao()), eq(paginacao.toPaginacao()))).thenReturn(paginaVazia);

        ListarVeiculosService service = new ListarVeiculosService(veiculoRepository);

        ListarVeiculosOutput resultado = service.executar(input, paginacao);

        assertEquals(List.of(), resultado.veiculos());
        assertEquals(PaginacaoOutput.de(paginaVazia), resultado.paginacao());

        verify(veiculoRepository).buscar(isNull(), eq(input.marca()), eq(input.modelo()), eq(input.anoFabricacao()), eq(paginacao.toPaginacao()));
        verifyNoMoreInteractions(veiculoRepository);
        verifyNoInteractions(veiculo);
    }

    @Test
    @DisplayName("Deve listar sem filtro de placa quando ela estiver em branco")
    void deveListarVeiculosComPlacaEmBranco() {
        ListarVeiculosInput input = new ListarVeiculosInput("   ", null, null, null);
        PaginacaoInput paginacao = new PaginacaoInput(0, 10);

        Pagina<Veiculo> paginaVazia = new Pagina<>(List.of(), 0, 10, 0, 0);

        when(veiculoRepository.buscar(isNull(), isNull(), isNull(), isNull(), eq(paginacao.toPaginacao()))).thenReturn(paginaVazia);

        ListarVeiculosService service = new ListarVeiculosService(veiculoRepository);

        ListarVeiculosOutput resultado = service.executar(input, paginacao);

        assertEquals(List.of(), resultado.veiculos());
        assertEquals(PaginacaoOutput.de(paginaVazia), resultado.paginacao());

        verify(veiculoRepository).buscar(isNull(), isNull(), isNull(), isNull(), eq(paginacao.toPaginacao()));
        verifyNoMoreInteractions(veiculoRepository);
        verifyNoInteractions(veiculo);
    }
}