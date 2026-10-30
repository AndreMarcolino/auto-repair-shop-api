package br.com.autorepairshop.application.ordemservico.reprovar;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.infrastructure.security.CodificadorCodigoAcesso;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReprovarOrdemServicoServiceTest {

    private static final UUID CLIENTE_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    private static final UUID ORDEM_SERVICO_ID = UUID.fromString("b92e5ed1-a7ca-4b1d-8398-0e5d5c440029");

    private static final String CPF_VALIDO = "52998224725";
    private static final String NUMERO_ORDEM_SERVICO = "OS-001";
    private static final String CODIGO_VERIFICACAO = "123456";

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private HistoricoStatusOrdemServicoRepository historicoRepository;

    @Mock
    private CodificadorCodigoAcesso codificadorCodigoAcesso;

    @Mock
    private Cliente cliente;

    @Mock
    private OrdemServico ordemServico;

    @Mock
    private Orcamento orcamento;

    @Mock
    private HistoricoStatusOrdemServico historico;

    @InjectMocks
    private ReprovarOrdemServicoService service;

    @Test
    @DisplayName("Deve reprovar o orçamento e a ordem de serviço")
    void deveReprovarOrdemServico() {
        ReprovarOrdemServicoInput input = criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, CPF_VALIDO);

        when(clienteRepository.buscarPorCpfCnpj(any(CpfCnpj.class))).thenReturn(Optional.of(cliente));
        when(cliente.getId()).thenReturn(CLIENTE_ID);

        when(ordemServicoRepository.buscarPoNumero(NUMERO_ORDEM_SERVICO)).thenReturn(Optional.of(ordemServico));
        when(ordemServico.getClienteId()).thenReturn(CLIENTE_ID);
        when(ordemServico.getId()).thenReturn(ORDEM_SERVICO_ID);

        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID)).thenReturn(Optional.of(orcamento));

        try (MockedStatic<HistoricoStatusOrdemServico> historicoStatic = mockStatic(HistoricoStatusOrdemServico.class)) {

            historicoStatic.when(() -> HistoricoStatusOrdemServico.registrar(ORDEM_SERVICO_ID, null, null)).thenReturn(historico);

            service.executar(input);

            InOrder ordem = inOrder(orcamento, ordemServico, orcamentoRepository, ordemServicoRepository, historicoRepository);

            ordem.verify(orcamento).reprovar();
            ordem.verify(ordemServico).rejeitar();
            ordem.verify(orcamentoRepository).salvar(orcamento);
            ordem.verify(ordemServicoRepository).salvar(ordemServico);
            ordem.verify(historicoRepository).salvar(historico);

            historicoStatic.verify(() -> HistoricoStatusOrdemServico.registrar(ORDEM_SERVICO_ID, null, null));
        }
    }

    @Test
    @DisplayName("Deve rejeitar CPF/CNPJ ausente")
    void deveRejeitarDocumentoAusente() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, null)));

        verificarNenhumaInteracaoComRepositorios();
    }

    @Test
    @DisplayName("Deve rejeitar documento em branco")
    void deveRejeitarDocumentoEmBranco() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, " ")));

        verificarNenhumaInteracaoComRepositorios();
    }

    @Test
    @DisplayName("Deve rejeitar código de verificação ausente")
    void deveRejeitarCodigoVerificacaoAusente() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, null, CPF_VALIDO)));

        verificarNenhumaInteracaoComRepositorios();
    }

    @Test
    @DisplayName("Deve rejeitar código de verificação em branco")
    void deveRejeitarCodigoVerificacaoEmBranco() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, " ", CPF_VALIDO)));

        verificarNenhumaInteracaoComRepositorios();
    }

    @Test
    @DisplayName("Deve rejeitar documento inválido")
    void deveRejeitarDocumentoInvalido() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, "123")));

        verificarNenhumaInteracaoComRepositorios();
    }

    @Test
    @DisplayName("Deve rejeitar cliente não encontrado")
    void deveRejeitarClienteNaoEncontrado() {
        when(clienteRepository.buscarPorCpfCnpj(any(CpfCnpj.class))).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, CPF_VALIDO)));

        verify(clienteRepository).buscarPorCpfCnpj(any(CpfCnpj.class));
        verifyNoInteractions(ordemServicoRepository, orcamentoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ordem de serviço não encontrada")
    void deveRejeitarOrdemServicoNaoEncontrada() {
        when(clienteRepository.buscarPorCpfCnpj(any(CpfCnpj.class))).thenReturn(Optional.of(cliente));
        when(ordemServicoRepository.buscarPoNumero(NUMERO_ORDEM_SERVICO)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, CPF_VALIDO)));

        verifyNoInteractions(orcamentoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar ordem de serviço de outro cliente")
    void deveRejeitarOrdemServicoDeOutroCliente() {
        when(clienteRepository.buscarPorCpfCnpj(any(CpfCnpj.class))).thenReturn(Optional.of(cliente));
        when(cliente.getId()).thenReturn(CLIENTE_ID);

        when(ordemServicoRepository.buscarPoNumero(NUMERO_ORDEM_SERVICO)).thenReturn(Optional.of(ordemServico));
        when(ordemServico.getClienteId()).thenReturn(UUID.randomUUID());

        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, CPF_VALIDO)));

        verifyNoInteractions(orcamentoRepository, historicoRepository);
        verify(ordemServicoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve rejeitar orçamento não encontrado")
    void deveRejeitarOrcamentoNaoEncontrado() {
        when(clienteRepository.buscarPorCpfCnpj(any(CpfCnpj.class))).thenReturn(Optional.of(cliente));
        when(cliente.getId()).thenReturn(CLIENTE_ID);

        when(ordemServicoRepository.buscarPoNumero(NUMERO_ORDEM_SERVICO)).thenReturn(Optional.of(ordemServico));
        when(ordemServico.getClienteId()).thenReturn(CLIENTE_ID);
        when(ordemServico.getId()).thenReturn(ORDEM_SERVICO_ID);

        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, CPF_VALIDO)));

        verify(orcamentoRepository, never()).salvar(any());
        verify(ordemServicoRepository, never()).salvar(any());
        verifyNoInteractions(historicoRepository);
    }

    private void verificarNenhumaInteracaoComRepositorios() {
        verifyNoInteractions(clienteRepository, ordemServicoRepository, orcamentoRepository, historicoRepository);
    }

    private ReprovarOrdemServicoInput criarInput(String numeroOrdemServico, String codigoVerificacao, String cpfCnpj) {

        return new ReprovarOrdemServicoInput(numeroOrdemServico, codigoVerificacao, cpfCnpj);
    }
}