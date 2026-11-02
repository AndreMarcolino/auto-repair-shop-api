package br.com.autorepairshop.application.ordemservico.calculartempomedioexecucao;

import java.lang.reflect.RecordComponent;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalcularTempoMedioExecucaoServiceTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private OrdemServico ordemServico;

    @InjectMocks
    private CalcularTempoMedioExecucaoService service;

    @Test
    @DisplayName("Deve calcular os tempos médios das ordens entregues")
    void deveCalcularTemposMediosDasOrdensEntregues() {
        when(ordemServicoRepository.buscarPorStatus(StatusOrdemServico.ENTREGUE))
                .thenReturn(List.of(ordemServico));

        when(ordemServico.getNumero()).thenReturn("OS-2026-000001");
        when(ordemServico.calcularTempoTotalAtendimento())
                .thenReturn(Optional.of(Duration.ofHours(2)));
        when(ordemServico.calcularTempoExecucao())
                .thenReturn(Optional.of(Duration.ofHours(1)));

        CalcularTempoMedioExecucaoOutput resultado = service.executar();

        assertNotNull(resultado);

        // Quantidades e médias do resultado geral.
        assertNumero(1L, componente(resultado, 0));
        assertNumero(1L, componente(resultado, 1));
        assertEquals("120.00", componente(resultado, 2).toString());
        assertEquals("0.08", componente(resultado, 3).toString());
        assertNumero(1L, componente(resultado, 4));
        assertEquals("60.00", componente(resultado, 5).toString());
        assertEquals("0.04", componente(resultado, 6).toString());

        @SuppressWarnings("unchecked")
        List<Object> itens = (List<Object>) componente(resultado, 7);

        assertEquals(1, itens.size());

        Object item = itens.get(0);

        // Número da OS e tempos calculados para o item.
        assertEquals("OS-2026-000001", componente(item, 0));
        assertEquals("120.00", componente(item, 1).toString());
        assertEquals("0.08", componente(item, 2).toString());
        assertEquals("60.00", componente(item, 3).toString());
        assertEquals("0.04", componente(item, 4).toString());

        verify(ordemServicoRepository)
                .buscarPorStatus(StatusOrdemServico.ENTREGUE);
    }

    @Test
    @DisplayName("Deve retornar médias zeradas quando não houver ordens entregues")
    void deveRetornarMediasZeradasSemOrdensEntregues() {
        when(ordemServicoRepository.buscarPorStatus(StatusOrdemServico.ENTREGUE))
                .thenReturn(List.of());

        CalcularTempoMedioExecucaoOutput resultado = service.executar();

        assertNotNull(resultado);

        assertNumero(0L, componente(resultado, 0));
        assertNumero(0L, componente(resultado, 1));
        assertEquals("0.00", componente(resultado, 2).toString());
        assertEquals("0.00", componente(resultado, 3).toString());
        assertNumero(0L, componente(resultado, 4));
        assertEquals("0.00", componente(resultado, 5).toString());
        assertEquals("0.00", componente(resultado, 6).toString());

        @SuppressWarnings("unchecked")
        List<Object> itens = (List<Object>) componente(resultado, 7);

        assertEquals(List.of(), itens);

        verify(ordemServicoRepository)
                .buscarPorStatus(StatusOrdemServico.ENTREGUE);
        verifyNoInteractions(ordemServico);
    }

    @Test
    @DisplayName("Deve calcular as médias somente com os tempos disponíveis")
    void deveIgnorarTemposAusentesNoCalculoDasMedias() {
        when(ordemServicoRepository.buscarPorStatus(StatusOrdemServico.ENTREGUE))
                .thenReturn(List.of(ordemServico));

        when(ordemServico.getNumero()).thenReturn("OS-2026-000002");
        when(ordemServico.calcularTempoTotalAtendimento())
                .thenReturn(Optional.empty());
        when(ordemServico.calcularTempoExecucao())
                .thenReturn(Optional.of(Duration.ofMinutes(30)));

        CalcularTempoMedioExecucaoOutput resultado = service.executar();

        assertNotNull(resultado);

        assertNumero(1L, componente(resultado, 0));
        assertNumero(0L, componente(resultado, 1));
        assertEquals("0.00", componente(resultado, 2).toString());
        assertEquals("0.00", componente(resultado, 3).toString());
        assertNumero(1L, componente(resultado, 4));
        assertEquals("30.00", componente(resultado, 5).toString());
        assertEquals("0.02", componente(resultado, 6).toString());

        @SuppressWarnings("unchecked")
        List<Object> itens = (List<Object>) componente(resultado, 7);

        assertEquals(1, itens.size());

        Object item = itens.get(0);
        assertEquals("OS-2026-000002", componente(item, 0));
        assertNull(componente(item, 1));
        assertNull(componente(item, 2));
        assertEquals("30.00", componente(item, 3).toString());
        assertEquals("0.02", componente(item, 4).toString());
    }

    private void assertNumero(long esperado, Object valorReal) {
        if (!(valorReal instanceof Number numero)) {
            throw new AssertionError(
                    "Era esperado um valor numérico, mas foi recebido: "
                            + (valorReal == null
                            ? "null"
                            : valorReal.getClass().getName())
            );
        }

        assertEquals(esperado, numero.longValue());
    }

    private Object componente(Object record, int indice) {
        try {
            RecordComponent[] componentes =
                    record.getClass().getRecordComponents();

            if (componentes == null
                    || indice < 0
                    || indice >= componentes.length) {
                throw new AssertionError(
                        "O objeto esperado não é um record ou o índice é inválido: "
                                + indice
                );
            }

            return componentes[indice].getAccessor().invoke(record);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(
                    "Não foi possível ler o componente " + indice + " do record",
                    e
            );
        }
    }
}