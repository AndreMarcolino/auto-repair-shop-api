package br.com.autorepairshop.application.insumo.listar;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.application.insumo.shared.InsumoOutput;
import br.com.autorepairshop.application.shared.dto.PaginacaoInput;
import br.com.autorepairshop.application.shared.dto.PaginacaoOutput;
import br.com.autorepairshop.domain.insumo.Insumo;
import br.com.autorepairshop.domain.insumo.InsumoRepository;
import br.com.autorepairshop.domain.shared.Pagina;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarInsumosServiceTest {

    private static final String DESCRICAO = "Óleo de motor";
    private static final BigDecimal VALOR_REFERENCIA =
            new BigDecimal("39.90");
    private static final Integer QUANTIDADE_ESTOQUE = 15;

    private static final int NUMERO_PAGINA = 0;
    private static final int TAMANHO_PAGINA = 10;

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private Insumo insumo;

    @Mock
    private InsumoOutput insumoOutput;

    @Mock
    private PaginacaoOutput paginacaoOutput;

    @InjectMocks
    private ListarInsumosService service;

    @Test
    @DisplayName("Deve listar insumos aplicando os filtros informados")
    void deveListarInsumosComFiltros() {
        ListarInsumosInput input = new ListarInsumosInput(
                DESCRICAO,
                VALOR_REFERENCIA,
                QUANTIDADE_ESTOQUE
        );
        PaginacaoInput paginacaoInput =
                new PaginacaoInput(NUMERO_PAGINA, TAMANHO_PAGINA);

        Pagina<Insumo> pagina = criarPagina(List.of(insumo));

        when(insumoRepository.buscar(
                DESCRICAO,
                VALOR_REFERENCIA,
                QUANTIDADE_ESTOQUE,
                paginacaoInput.toPaginacao()))
                .thenReturn(pagina);

        try (MockedStatic<InsumoOutput> conversaoInsumo =
                     mockStatic(InsumoOutput.class);
             MockedStatic<PaginacaoOutput> conversaoPaginacao =
                     mockStatic(PaginacaoOutput.class)) {

            conversaoInsumo.when(() -> InsumoOutput.fromDomain(insumo))
                    .thenReturn(insumoOutput);
            conversaoPaginacao.when(() -> PaginacaoOutput.de(pagina))
                    .thenReturn(paginacaoOutput);

            ListarInsumosOutput resultado =
                    service.executar(input, paginacaoInput);

            assertEquals(List.of(insumoOutput), resultado.insumos());
            assertSame(paginacaoOutput, resultado.paginacao());

            verify(insumoRepository).buscar(
                    DESCRICAO,
                    VALOR_REFERENCIA,
                    QUANTIDADE_ESTOQUE,
                    paginacaoInput.toPaginacao());
            conversaoInsumo.verify(() -> InsumoOutput.fromDomain(insumo));
            conversaoPaginacao.verify(() -> PaginacaoOutput.de(pagina));
        }
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver insumos")
    void deveRetornarListaVazia() {
        ListarInsumosInput input = new ListarInsumosInput(
                null,
                null,
                null
        );
        PaginacaoInput paginacaoInput =
                new PaginacaoInput(NUMERO_PAGINA, TAMANHO_PAGINA);

        Pagina<Insumo> pagina = criarPagina(List.of());

        when(insumoRepository.buscar(
                null,
                null,
                null,
                paginacaoInput.toPaginacao()))
                .thenReturn(pagina);

        try (MockedStatic<PaginacaoOutput> conversaoPaginacao =
                     mockStatic(PaginacaoOutput.class)) {

            conversaoPaginacao.when(() -> PaginacaoOutput.de(pagina))
                    .thenReturn(paginacaoOutput);

            ListarInsumosOutput resultado =
                    service.executar(input, paginacaoInput);

            assertNotNull(resultado);
            assertTrue(resultado.insumos().isEmpty());
            assertSame(paginacaoOutput, resultado.paginacao());

            verify(insumoRepository).buscar(
                    null,
                    null,
                    null,
                    paginacaoInput.toPaginacao());
            conversaoPaginacao.verify(() -> PaginacaoOutput.de(pagina));
        }
    }

    private Pagina<Insumo> criarPagina(List<Insumo> insumos) {
        return new Pagina<>(
                insumos,
                NUMERO_PAGINA,
                TAMANHO_PAGINA,
                insumos.size(),
                insumos.isEmpty() ? 0 : 1
        );
    }
}