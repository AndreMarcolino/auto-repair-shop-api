package br.com.autorepairshop.application.servico.buscarservicoporid;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.application.servico.shared.ServicoOutput;
import br.com.autorepairshop.domain.servico.Servico;
import br.com.autorepairshop.domain.servico.ServicoRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarServicoPorIdServiceTest {

    private static final UUID SERVICO_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private Servico servico;

    @Test
    @DisplayName("Deve retornar o serviço quando encontrado")
    void deveRetornarServicoQuandoEncontrado() {
        when(servicoRepository.buscarPorId(SERVICO_ID)).thenReturn(Optional.of(servico));

        BuscarServicoPorIdService service = new BuscarServicoPorIdService(servicoRepository);

        Optional<ServicoOutput> resultado = service.executar(SERVICO_ID.toString());

        assertEquals(Optional.of(ServicoOutput.fromDomain(servico)), resultado);
        verify(servicoRepository).buscarPorId(SERVICO_ID);
        verifyNoMoreInteractions(servicoRepository);
    }

    @Test
    @DisplayName("Deve retornar vazio quando o serviço não for encontrado")
    void deveRetornarVazioQuandoServicoNaoEncontrado() {
        when(servicoRepository.buscarPorId(SERVICO_ID)).thenReturn(Optional.empty());

        BuscarServicoPorIdService service = new BuscarServicoPorIdService(servicoRepository);

        Optional<ServicoOutput> resultado = service.executar(SERVICO_ID.toString());

        assertEquals(Optional.empty(), resultado);
        verify(servicoRepository).buscarPorId(SERVICO_ID);
        verifyNoMoreInteractions(servicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ID nulo")
    void deveRejeitarIdNulo() {
        BuscarServicoPorIdService service = new BuscarServicoPorIdService(servicoRepository);

        assertThrows(DomainException.class, () -> service.executar(null));

        verifyNoInteractions(servicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ID inválido")
    void deveRejeitarIdInvalido() {
        BuscarServicoPorIdService service = new BuscarServicoPorIdService(servicoRepository);

        assertThrows(DomainException.class, () -> service.executar("id-invalido"));

        verifyNoInteractions(servicoRepository);
    }
}