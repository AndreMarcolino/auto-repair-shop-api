package br.com.autorepairshop.application.insumo.criar;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.application.estoque.registrarentrada.RegistrarEntradaEstoqueInput;
import br.com.autorepairshop.application.estoque.registrarentrada.RegistrarEntradaEstoqueUseCase;
import br.com.autorepairshop.domain.insumo.Insumo;
import br.com.autorepairshop.domain.insumo.InsumoRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarInsumoServiceTest {

    private static final String DESCRICAO = "Óleo de motor";
    private static final BigDecimal VALOR_REFERENCIA = new BigDecimal("39.90");
    private static final UUID ID_INSUMO = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private RegistrarEntradaEstoqueUseCase registrarEntradaEstoqueUseCase;

    @Mock
    private Insumo insumoSalvo;

    @Mock
    private CriarInsumoOutput output;

    @InjectMocks
    private CriarInsumoService service;

    @Test
    @DisplayName("Deve criar, salvar e registrar entrada inicial zerada")
    void deveCriarInsumoERegistrarEntradaInicial() {
        CriarInsumoInput input = new CriarInsumoInput(DESCRICAO, VALOR_REFERENCIA);

        when(insumoRepository.salvar(any(Insumo.class))).thenReturn(insumoSalvo);
        when(insumoSalvo.getId()).thenReturn(ID_INSUMO);

        try (MockedStatic<CriarInsumoOutput> conversao = mockStatic(CriarInsumoOutput.class)) {

            conversao.when(() -> CriarInsumoOutput.fromDomain(insumoSalvo)).thenReturn(output);

            CriarInsumoOutput resultado = service.executar(input);

            assertSame(output, resultado);

            ArgumentCaptor<Insumo> insumoCaptor = ArgumentCaptor.forClass(Insumo.class);
            verify(insumoRepository).salvar(insumoCaptor.capture());

            Insumo insumoCriado = insumoCaptor.getValue();
            assertNotNull(insumoCriado);
            assertEquals(DESCRICAO, insumoCriado.getDescricao());
            assertEquals(VALOR_REFERENCIA, insumoCriado.getValorReferencia());

            ArgumentCaptor<RegistrarEntradaEstoqueInput> entradaCaptor = ArgumentCaptor.forClass(RegistrarEntradaEstoqueInput.class);
            verify(registrarEntradaEstoqueUseCase).executar(entradaCaptor.capture());

            RegistrarEntradaEstoqueInput entrada = entradaCaptor.getValue();
            assertEquals(ID_INSUMO.toString(), entrada.insumoId());
            assertEquals(0, BigDecimal.ZERO.compareTo(entrada.quantidade()));

            conversao.verify(() -> CriarInsumoOutput.fromDomain(insumoSalvo));
        }
    }

    @Test
    @DisplayName("Não deve registrar entrada nem gerar output se salvar falhar")
    void naoDeveContinuarQuandoSalvarFalhar() {
        CriarInsumoInput input = new CriarInsumoInput(DESCRICAO, VALOR_REFERENCIA);

        when(insumoRepository.salvar(any(Insumo.class))).thenThrow(new IllegalStateException("Falha ao salvar"));

        assertThrows(IllegalStateException.class, () -> service.executar(input));

        verify(insumoRepository).salvar(any(Insumo.class));
        verifyNoInteractions(registrarEntradaEstoqueUseCase);
    }

    @Test
    @DisplayName("Deve propagar falha ao registrar entrada inicial")
    void devePropagarFalhaAoRegistrarEntrada() {
        CriarInsumoInput input = new CriarInsumoInput(DESCRICAO, VALOR_REFERENCIA);

        when(insumoRepository.salvar(any(Insumo.class))).thenReturn(insumoSalvo);
        when(insumoSalvo.getId()).thenReturn(ID_INSUMO);

        doThrow(new IllegalStateException("Falha ao registrar entrada")).when(registrarEntradaEstoqueUseCase).executar(any(RegistrarEntradaEstoqueInput.class));

        assertThrows(IllegalStateException.class, () -> service.executar(input));

        verify(insumoRepository).salvar(any(Insumo.class));
        verify(registrarEntradaEstoqueUseCase).executar(any(RegistrarEntradaEstoqueInput.class));
    }
}