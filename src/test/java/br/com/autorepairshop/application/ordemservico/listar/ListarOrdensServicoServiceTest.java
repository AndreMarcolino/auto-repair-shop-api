package br.com.autorepairshop.application.ordemservico.listar;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarOrdensServicoServiceTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private OrdemServico ordemMaisTardia;

    @Mock
    private OrdemServico ordemMaisAntiga;

    @Mock
    private OrdemServico ordemStatusPosterior;

    @Test
    @DisplayName("Deve listar as ordens ordenadas por status e data de abertura")
    void deveListarOrdensOrdenadasPorStatusEData() {
        List<StatusOrdemServico> statuses = List.of(StatusOrdemServico.values());

        if (statuses.size() < 2) {
            throw new IllegalStateException("São necessários pelo menos dois status para este teste.");
        }

        StatusOrdemServico primeiroStatus = statuses.get(0);
        StatusOrdemServico segundoStatus = statuses.get(1);

        UUID idMaisTardia = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");
        UUID idMaisAntiga = UUID.fromString("b92e5ed1-a7ca-4b1d-8398-0e5d5c440029");
        UUID idStatusPosterior = UUID.fromString("c92e5ed1-a7ca-4b1d-8398-0e5d5c440030");

        UUID clienteId = UUID.fromString("d92e5ed1-a7ca-4b1d-8398-0e5d5c440031");
        UUID veiculoId = UUID.fromString("e92e5ed1-a7ca-4b1d-8398-0e5d5c440032");

        LocalDateTime dataMaisAntiga = LocalDateTime.of(2026, 1, 10, 9, 0);
        LocalDateTime dataMaisTardia = LocalDateTime.of(2026, 1, 11, 9, 0);
        LocalDateTime dataStatusPosterior = LocalDateTime.of(2026, 1, 9, 9, 0);

        configurarOrdem(ordemMaisTardia, idMaisTardia, "OS-002", clienteId, veiculoId, primeiroStatus, dataMaisTardia);

        configurarOrdem(ordemMaisAntiga, idMaisAntiga, "OS-001", clienteId, veiculoId, primeiroStatus, dataMaisAntiga);

        configurarOrdem(ordemStatusPosterior, idStatusPosterior, "OS-003", clienteId, veiculoId, segundoStatus, dataStatusPosterior);

        when(ordemServicoRepository.buscarPorStatus(statuses)).thenReturn(List.of(ordemStatusPosterior, ordemMaisTardia, ordemMaisAntiga));

        ListarOrdensServicoService service = new ListarOrdensServicoService(ordemServicoRepository);

        List<ListarOrdensServicoOutput> resultado = service.executar();

        assertEquals(3, resultado.size());
        assertEquals(idMaisAntiga, resultado.get(0).id());
        assertEquals(idMaisTardia, resultado.get(1).id());
        assertEquals(idStatusPosterior, resultado.get(2).id());

        verify(ordemServicoRepository).buscarPorStatus(statuses);
        verifyNoMoreInteractions(ordemServicoRepository);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver ordens")
    void deveRetornarListaVaziaQuandoNaoHouverOrdens() {
        List<StatusOrdemServico> statuses = List.of(StatusOrdemServico.values());

        when(ordemServicoRepository.buscarPorStatus(statuses)).thenReturn(List.of());

        ListarOrdensServicoService service = new ListarOrdensServicoService(ordemServicoRepository);

        List<ListarOrdensServicoOutput> resultado = service.executar();

        assertEquals(List.of(), resultado);

        verify(ordemServicoRepository).buscarPorStatus(statuses);
        verifyNoMoreInteractions(ordemServicoRepository);
    }

    private void configurarOrdem(OrdemServico ordem, UUID id, String numero, UUID clienteId, UUID veiculoId, StatusOrdemServico status, LocalDateTime dataAbertura) {

        when(ordem.getId()).thenReturn(id);
        when(ordem.getNumero()).thenReturn(numero);
        when(ordem.getClienteId()).thenReturn(clienteId);
        when(ordem.getVeiculoId()).thenReturn(veiculoId);
        when(ordem.getStatus()).thenReturn(status);
        when(ordem.getDataAbertura()).thenReturn(dataAbertura);
    }
}