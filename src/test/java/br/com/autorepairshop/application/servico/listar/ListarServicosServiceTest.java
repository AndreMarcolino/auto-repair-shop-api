package br.com.autorepairshop.application.servico.listar;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.application.servico.shared.ServicoOutput;
import br.com.autorepairshop.application.shared.dto.PaginacaoInput;
import br.com.autorepairshop.application.shared.dto.PaginacaoOutput;
import br.com.autorepairshop.domain.servico.Servico;
import br.com.autorepairshop.domain.servico.ServicoRepository;
import br.com.autorepairshop.domain.shared.Pagina;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarServicosServiceTest {

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private Servico servico;

    @Test
    @DisplayName("Deve listar serviços e retornar os dados de paginação")
    void deveListarServicosComPaginacao() {
        ListarServicosInput input = new ListarServicosInput("Troca de óleo", new BigDecimal("150.00"));
        PaginacaoInput paginacaoInput = new PaginacaoInput(0, 10);

        Pagina<Servico> pagina = new Pagina<>(List.of(servico), 0, 10, 1, 1);

        when(servicoRepository.buscar(input.descricao(), input.valorReferencia(), paginacaoInput.toPaginacao())).thenReturn(pagina);

        ListarServicosService service = new ListarServicosService(servicoRepository);

        ListarServicosOutput resultado = service.executar(input, paginacaoInput);

        assertEquals(1, resultado.servicos().size());
        assertEquals(ServicoOutput.fromDomain(servico), resultado.servicos().get(0));
        assertEquals(PaginacaoOutput.de(pagina), resultado.paginacao());

        verify(servicoRepository).buscar(input.descricao(), input.valorReferencia(), paginacaoInput.toPaginacao());
        verifyNoMoreInteractions(servicoRepository);
    }

    @Test
    @DisplayName("Deve retornar lista vazia mantendo os dados de paginação")
    void deveRetornarListaVazia() {
        ListarServicosInput input = new ListarServicosInput(null, null);
        PaginacaoInput paginacaoInput = new PaginacaoInput(0, 10);

        Pagina<Servico> paginaVazia = new Pagina<>(List.of(), 0, 10, 0, 0);

        when(servicoRepository.buscar(input.descricao(), input.valorReferencia(), paginacaoInput.toPaginacao())).thenReturn(paginaVazia);

        ListarServicosService service = new ListarServicosService(servicoRepository);

        ListarServicosOutput resultado = service.executar(input, paginacaoInput);

        assertEquals(List.of(), resultado.servicos());
        assertEquals(PaginacaoOutput.de(paginaVazia), resultado.paginacao());

        verify(servicoRepository).buscar(input.descricao(), input.valorReferencia(), paginacaoInput.toPaginacao());
        verifyNoMoreInteractions(servicoRepository);
    }
}