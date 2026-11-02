package br.com.autorepairshop.application.ordemservico.detalharcliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
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
import br.com.autorepairshop.infrastructure.security.CodificadorCodigoAcesso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetalharOrdemServicoClienteServiceTest {

    private static final UUID CLIENTE_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    private static final UUID VEICULO_ID = UUID.fromString("b92e5ed1-a7ca-4b1d-8398-0e5d5c440029");

    private static final UUID ORDEM_SERVICO_ID = UUID.fromString("c92e5ed1-a7ca-4b1d-8398-0e5d5c440030");

    private static final String NUMERO_OS = "OS-2026-000001";
    private static final String CODIGO_VERIFICACAO = "123456";
    private static final String CPF_VALIDO = "52998224725";
    private static final String CODIGO_HASH = "hash-codigo";

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private CodificadorCodigoAcesso codificadorCodigoAcesso;

    @Mock
    private Cliente cliente;

    @Mock
    private OrdemServico ordemServico;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Veiculo veiculo;

    @Mock
    private Orcamento orcamento;

    @InjectMocks
    private DetalharOrdemServicoClienteService service;

    @Test
    @DisplayName("Deve rejeitar CPF/CNPJ nulo")
    void deveRejeitarDocumentoNulo() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_OS, CODIGO_VERIFICACAO, null)));

        verificarNenhumaInteracao();
    }

    @Test
    @DisplayName("Deve rejeitar CPF/CNPJ em branco")
    void deveRejeitarDocumentoEmBranco() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_OS, CODIGO_VERIFICACAO, " ")));

        verificarNenhumaInteracao();
    }

    @Test
    @DisplayName("Deve rejeitar código de verificação nulo")
    void deveRejeitarCodigoVerificacaoNulo() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_OS, null, CPF_VALIDO)));

        verificarNenhumaInteracao();
    }

    @Test
    @DisplayName("Deve rejeitar código de verificação em branco")
    void deveRejeitarCodigoVerificacaoEmBranco() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_OS, " ", CPF_VALIDO)));

        verificarNenhumaInteracao();
    }

    @Test
    @DisplayName("Deve rejeitar CPF/CNPJ com formato inválido")
    void deveRejeitarDocumentoInvalido() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_OS, CODIGO_VERIFICACAO, "123")));

        verificarNenhumaInteracao();
    }

    @Test
    @DisplayName("Deve rejeitar quando o cliente não for encontrado")
    void deveRejeitarClienteNaoEncontrado() {
        when(clienteRepository.buscarPorCpfCnpj(any())).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_OS, CODIGO_VERIFICACAO, CPF_VALIDO)));

        verify(clienteRepository).buscarPorCpfCnpj(any());
        verifyNoInteractions(ordemServicoRepository, veiculoRepository, orcamentoRepository, codificadorCodigoAcesso);
    }

    @Test
    @DisplayName("Deve retornar nulo quando a ordem de serviço não for encontrada")
    void deveRetornarNuloQuandoOrdemServicoNaoEncontrada() {
        prepararBuscaDoCliente();
        when(ordemServicoRepository.buscarPoNumero(NUMERO_OS)).thenReturn(Optional.empty());

        DetalharOrdemServicoClienteOutput resultado = service.executar(criarInput(NUMERO_OS, CODIGO_VERIFICACAO, CPF_VALIDO));

        assertNull(resultado);

        verify(clienteRepository).buscarPorCpfCnpj(any());
        verify(ordemServicoRepository).buscarPoNumero(NUMERO_OS);
        verifyNoInteractions(veiculoRepository, orcamentoRepository, codificadorCodigoAcesso);
    }

    @Test
    @DisplayName("Deve rejeitar ordem de serviço pertencente a outro cliente")
    void deveRejeitarOrdemServicoDeOutroCliente() {
        prepararBuscaDoCliente();
        prepararBuscaDaOrdemServico();

        when(cliente.getId()).thenReturn(CLIENTE_ID);
        when(ordemServico.getClienteId()).thenReturn(VEICULO_ID);

        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_OS, CODIGO_VERIFICACAO, CPF_VALIDO)));

        verifyNoInteractions(veiculoRepository, orcamentoRepository, codificadorCodigoAcesso);
    }

    @Test
    @DisplayName("Deve rejeitar código de acesso incorreto")
    void deveRejeitarCodigoAcessoIncorreto() {
        prepararBuscaDoCliente();
        prepararBuscaDaOrdemServico();

        when(cliente.getId()).thenReturn(CLIENTE_ID);
        when(ordemServico.getClienteId()).thenReturn(CLIENTE_ID);
        when(codificadorCodigoAcesso.codificar(CODIGO_VERIFICACAO)).thenReturn("hash-diferente");
        when(ordemServico.getCodigoAcessoHash()).thenReturn(CODIGO_HASH);

        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_OS, CODIGO_VERIFICACAO, CPF_VALIDO)));

        verifyNoInteractions(veiculoRepository, orcamentoRepository);
    }

    @Test
    @DisplayName("Deve detalhar a ordem de serviço quando não houver orçamento")
    void deveDetalharOrdemServicoSemOrcamento() {
        prepararClienteEOrdemServicoParaDetalhamento();
        prepararCodigoValido();
        prepararVeiculo();

        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID)).thenReturn(Optional.empty());

        DetalharOrdemServicoClienteOutput resultado = service.executar(criarInput(NUMERO_OS, CODIGO_VERIFICACAO, CPF_VALIDO));

        assertNotNull(resultado);
        assertEquals(NUMERO_OS, componente(resultado, 0));

        Object clienteOutput = componente(resultado, 7);
        assertEquals("Cliente Teste", componente(clienteOutput, 0));

        Object veiculoOutput = componente(resultado, 8);
        assertEquals("ABC1D23", componente(veiculoOutput, 0));
        assertEquals("Toyota", componente(veiculoOutput, 1));
        assertEquals("Corolla", componente(veiculoOutput, 2));

        assertNull(componente(resultado, 9));

        verify(veiculoRepository).buscarPorId(VEICULO_ID);
        verify(orcamentoRepository).buscarPorOrdemServicoId(ORDEM_SERVICO_ID);
    }

    @Test
    @DisplayName("Deve detalhar a ordem de serviço com orçamento sem itens")
    void deveDetalharOrdemServicoComOrcamentoVazio() {
        prepararClienteEOrdemServicoParaDetalhamento();
        prepararCodigoValido();
        prepararVeiculo();

        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID)).thenReturn(Optional.of(orcamento));
        when(orcamento.getDiagnostico()).thenReturn("Diagnóstico do veículo");
        when(orcamento.getServicos()).thenReturn(List.of());
        when(orcamento.getInsumos()).thenReturn(List.of());

        DetalharOrdemServicoClienteOutput resultado = service.executar(criarInput(NUMERO_OS, CODIGO_VERIFICACAO, CPF_VALIDO));

        assertNotNull(resultado);

        Object orcamentoOutput = componente(resultado, 9);
        assertNotNull(orcamentoOutput);
        assertEquals("Diagnóstico do veículo", componente(orcamentoOutput, 0));
        assertEquals(0, new java.math.BigDecimal("0.00").compareTo((java.math.BigDecimal) componente(orcamentoOutput, 1)));
        assertEquals(List.of(), componente(orcamentoOutput, 2));
        assertEquals(List.of(), componente(orcamentoOutput, 3));
    }

    private void prepararBuscaDoCliente() {
        when(clienteRepository.buscarPorCpfCnpj(any())).thenReturn(Optional.of(cliente));
    }

    private void prepararBuscaDaOrdemServico() {
        when(ordemServicoRepository.buscarPoNumero(NUMERO_OS)).thenReturn(Optional.of(ordemServico));
    }

    private void prepararClienteEOrdemServicoParaDetalhamento() {
        prepararBuscaDoCliente();
        prepararBuscaDaOrdemServico();

        lenient().when(cliente.getId()).thenReturn(CLIENTE_ID);
        lenient().when(cliente.getNome()).thenReturn("Cliente Teste");
        lenient().when(ordemServico.getClienteId()).thenReturn(CLIENTE_ID);
        lenient().when(ordemServico.getVeiculoId()).thenReturn(VEICULO_ID);
        lenient().when(ordemServico.getId()).thenReturn(ORDEM_SERVICO_ID);
        lenient().when(ordemServico.getNumero()).thenReturn(NUMERO_OS);
    }

    private void prepararCodigoValido() {
        when(codificadorCodigoAcesso.codificar(CODIGO_VERIFICACAO)).thenReturn(CODIGO_HASH);
        when(ordemServico.getCodigoAcessoHash()).thenReturn(CODIGO_HASH);
    }

    private void prepararVeiculo() {
        when(veiculoRepository.buscarPorId(VEICULO_ID)).thenReturn(Optional.of(veiculo));
        when(veiculo.getPlaca().placa()).thenReturn("ABC1D23");
        when(veiculo.getMarca()).thenReturn("Toyota");
        when(veiculo.getModelo()).thenReturn("Corolla");
    }

    private void verificarNenhumaInteracao() {
        verifyNoInteractions(clienteRepository, ordemServicoRepository, veiculoRepository, orcamentoRepository, codificadorCodigoAcesso);
    }

    private DetalharOrdemServicoClienteInput criarInput(String numeroOrdemServico, String codigoVerificacao, String cpfCnpj) {

        return new DetalharOrdemServicoClienteInput(numeroOrdemServico, codigoVerificacao, cpfCnpj);
    }

    private Object componente(Object record, int indice) {
        try {
            var componentes = record.getClass().getRecordComponents();

            if (componentes == null || indice < 0 || indice >= componentes.length) {
                throw new AssertionError("O objeto não é um record ou o índice é inválido: " + indice);
            }

            return componentes[indice].getAccessor().invoke(record);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Não foi possível acessar o componente " + indice, e);
        }
    }
}