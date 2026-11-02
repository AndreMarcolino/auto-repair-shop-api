package br.com.autorepairshop.application.ordemservico.detalhar;

import java.lang.reflect.RecordComponent;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.veiculo.Veiculo;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetalharOrdemServicoServiceTest {

    private static final UUID ORDEM_SERVICO_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    private static final UUID CLIENTE_ID = UUID.fromString("b92e5ed1-a7ca-4b1d-8398-0e5d5c440029");

    private static final UUID VEICULO_ID = UUID.fromString("c92e5ed1-a7ca-4b1d-8398-0e5d5c440030");

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private OrdemServico ordemServico;

    @Mock
    private Cliente cliente;

    @Mock(answer = org.mockito.Answers.RETURNS_DEEP_STUBS)
    private Veiculo veiculo;

    @Mock
    private Orcamento orcamento;

    @InjectMocks
    private DetalharOrdemServicoService service;

    @Test
    @DisplayName("Deve rejeitar ID nulo")
    void deveRejeitarIdNulo() {
        assertThrows(DomainException.class, () -> service.executar(null));

        verifyNoInteractions(ordemServicoRepository, clienteRepository, veiculoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ID inválido")
    void deveRejeitarIdInvalido() {
        assertThrows(DomainException.class, () -> service.executar("id-invalido"));

        verifyNoInteractions(ordemServicoRepository, clienteRepository, veiculoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve retornar nulo quando a ordem de serviço não for encontrada")
    void deveRetornarNuloQuandoOrdemServicoNaoEncontrada() {
        when(ordemServicoRepository.buscarPorId(ORDEM_SERVICO_ID)).thenReturn(Optional.empty());

        DetalharOrdemServicoOutput resultado = service.executar(ORDEM_SERVICO_ID.toString());

        assertNull(resultado);

        verify(ordemServicoRepository).buscarPorId(ORDEM_SERVICO_ID);
        verifyNoInteractions(clienteRepository, veiculoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente da OS não for encontrado")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        prepararBuscaDaOrdemServico();
        when(ordemServico.getClienteId()).thenReturn(CLIENTE_ID);
        when(clienteRepository.buscarPorId(CLIENTE_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> service.executar(ORDEM_SERVICO_ID.toString()));

        verifyNoInteractions(veiculoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o veículo da OS não for encontrado")
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        prepararBuscaDaOrdemServico();
        when(ordemServico.getClienteId()).thenReturn(CLIENTE_ID);
        when(ordemServico.getVeiculoId()).thenReturn(VEICULO_ID);
        when(clienteRepository.buscarPorId(CLIENTE_ID)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(VEICULO_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> service.executar(ORDEM_SERVICO_ID.toString()));

        verifyNoInteractions(orcamentoRepository);
    }

    @Test
    @DisplayName("Deve detalhar a OS quando não houver orçamento")
    void deveDetalharOrdemServicoSemOrcamento() {
        prepararOrdemServicoCompleta();
        prepararClienteEVeiculo();

        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID)).thenReturn(Optional.empty());

        DetalharOrdemServicoOutput resultado = service.executar(ORDEM_SERVICO_ID.toString());

        assertNotNull(resultado);
        assertEquals(ORDEM_SERVICO_ID, componente(resultado, 0));

        Object clienteOutput = componente(resultado, 8);
        assertEquals(CLIENTE_ID, componente(clienteOutput, 0));
        assertEquals("Gisele Cliente", componente(clienteOutput, 1));

        Object veiculoOutput = componente(resultado, 9);
        assertEquals(VEICULO_ID, componente(veiculoOutput, 0));
        assertEquals("ABC1D23", componente(veiculoOutput, 1));
        assertEquals("Toyota", componente(veiculoOutput, 2));
        assertEquals("Corolla", componente(veiculoOutput, 3));

        assertNull(componente(resultado, 10));

        verify(orcamentoRepository).buscarPorOrdemServicoId(ORDEM_SERVICO_ID);
    }

    @Test
    @DisplayName("Deve detalhar o orçamento e calcular o valor total")
    void deveDetalharOrcamentoECalcularValorTotal() {
        prepararOrdemServicoCompleta();
        prepararClienteEVeiculo();

        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID)).thenReturn(Optional.of(orcamento));
        when(orcamento.getDiagnostico()).thenReturn("Revisão necessária");
        when(orcamento.getServicos()).thenReturn(List.of());
        when(orcamento.getInsumos()).thenReturn(List.of());

        DetalharOrdemServicoOutput resultado = service.executar(ORDEM_SERVICO_ID.toString());

        assertNotNull(resultado);

        Object orcamentoOutput = componente(resultado, 10);
        assertNotNull(orcamentoOutput);
        assertEquals("Revisão necessária", componente(orcamentoOutput, 0));
        assertEquals(BigDecimal.ZERO, componente(orcamentoOutput, 1));
        assertEquals(List.of(), componente(orcamentoOutput, 2));
        assertEquals(List.of(), componente(orcamentoOutput, 3));
    }

    private void prepararBuscaDaOrdemServico() {
        when(ordemServicoRepository.buscarPorId(ORDEM_SERVICO_ID)).thenReturn(Optional.of(ordemServico));
    }

    private void prepararOrdemServicoCompleta() {
        prepararBuscaDaOrdemServico();

        when(ordemServico.getId()).thenReturn(ORDEM_SERVICO_ID);
        when(ordemServico.getClienteId()).thenReturn(CLIENTE_ID);
        when(ordemServico.getVeiculoId()).thenReturn(VEICULO_ID);
        when(ordemServico.getNumero()).thenReturn("OS-2026-000001");
    }

    private void prepararClienteEVeiculo() {
        when(clienteRepository.buscarPorId(CLIENTE_ID)).thenReturn(Optional.of(cliente));
        when(cliente.getId()).thenReturn(CLIENTE_ID);
        when(cliente.getNome()).thenReturn("Gisele Cliente");

        when(veiculoRepository.buscarPorId(VEICULO_ID)).thenReturn(Optional.of(veiculo));
        when(veiculo.getId()).thenReturn(VEICULO_ID);
        when(veiculo.getPlaca().placa()).thenReturn("ABC1D23");
        when(veiculo.getMarca()).thenReturn("Toyota");
        when(veiculo.getModelo()).thenReturn("Corolla");
    }

    private Object componente(Object record, int indice) {
        try {
            RecordComponent[] componentes = record.getClass().getRecordComponents();

            if (componentes == null || indice < 0 || indice >= componentes.length) {
                throw new AssertionError("O objeto não é um record ou o índice é inválido: " + indice);
            }

            return componentes[indice].getAccessor().invoke(record);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Não foi possível acessar o componente " + indice, e);
        }
    }
}