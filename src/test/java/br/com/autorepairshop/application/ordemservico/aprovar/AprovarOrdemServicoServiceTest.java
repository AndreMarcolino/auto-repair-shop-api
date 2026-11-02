package br.com.autorepairshop.application.ordemservico.aprovar;

import java.util.List;
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
import br.com.autorepairshop.domain.estoque.EstoqueRepository;
import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoInsumoPendenteRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;
import br.com.autorepairshop.infrastructure.security.CodificadorCodigoAcesso;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AprovarOrdemServicoServiceTest {

    private static final UUID CLIENTE_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");
    private static final UUID ORDEM_SERVICO_ID = UUID.fromString("b92e5ed1-a7ca-4b1d-8398-0e5d5c440029");

    private static final String NUMERO_ORDEM_SERVICO = "OS-2026-000001";
    private static final String CODIGO_VERIFICACAO = "123456";
    private static final String CODIGO_HASH = "hash-codigo";
    private static final String CPF_VALIDO = "52998224725";

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private HistoricoStatusOrdemServicoRepository historicoRepository;

    @Mock
    private OrdemServicoInsumoPendenteRepository insumoPendenteRepository;

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
    private AprovarOrdemServicoService service;

    @Test
    @DisplayName("Deve aprovar a OS quando o orçamento não possui insumos")
    void deveAprovarOrdemServicoSemInsumos() {
        AprovarOrdemServicoInput input = criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, CPF_VALIDO);
        CpfCnpj cpfCnpj = criarCpfCnpjValido();

        when(clienteRepository.buscarPorCpfCnpj(cpfCnpj)).thenReturn(Optional.of(cliente));
        when(ordemServicoRepository.buscarPoNumero(NUMERO_ORDEM_SERVICO)).thenReturn(Optional.of(ordemServico));
        when(cliente.getId()).thenReturn(CLIENTE_ID);
        when(ordemServico.getClienteId()).thenReturn(CLIENTE_ID);
        when(codificadorCodigoAcesso.codificar(CODIGO_VERIFICACAO)).thenReturn(CODIGO_HASH);
        when(ordemServico.getCodigoAcessoHash()).thenReturn(CODIGO_HASH);
        when(ordemServico.getId()).thenReturn(ORDEM_SERVICO_ID);
        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID)).thenReturn(Optional.of(orcamento));
        when(orcamento.getInsumos()).thenReturn(List.of());

        try (MockedStatic<HistoricoStatusOrdemServico> historicoStatic = mockStatic(HistoricoStatusOrdemServico.class)) {

            historicoStatic.when(() -> HistoricoStatusOrdemServico.registrar(ORDEM_SERVICO_ID, null, null)).thenReturn(historico);

            service.executar(input);

            InOrder ordem = inOrder(orcamento, ordemServico, orcamentoRepository, ordemServicoRepository, historicoRepository);

            ordem.verify(orcamento).aprovar();
            ordem.verify(ordemServico).aprovar();
            ordem.verify(orcamentoRepository).salvar(orcamento);
            ordem.verify(ordemServicoRepository).salvar(ordemServico);
            ordem.verify(historicoRepository).salvar(historico);

            verifyNoInteractions(estoqueRepository, insumoPendenteRepository);

            historicoStatic.verify(() -> HistoricoStatusOrdemServico.registrar(ORDEM_SERVICO_ID, null, null));
        }
    }

    @Test
    @DisplayName("Deve rejeitar CPF/CNPJ nulo")
    void deveRejeitarDocumentoNulo() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, null)));

        verificarNenhumaInteracao();
    }

    @Test
    @DisplayName("Deve rejeitar CPF/CNPJ em branco")
    void deveRejeitarDocumentoEmBranco() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, " ")));

        verificarNenhumaInteracao();
    }

    @Test
    @DisplayName("Deve rejeitar código de verificação nulo")
    void deveRejeitarCodigoVerificacaoNulo() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, null, CPF_VALIDO)));

        verificarNenhumaInteracao();
    }

    @Test
    @DisplayName("Deve rejeitar código de verificação em branco")
    void deveRejeitarCodigoVerificacaoEmBranco() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, " ", CPF_VALIDO)));

        verificarNenhumaInteracao();
    }

    @Test
    @DisplayName("Deve rejeitar documento com formato inválido")
    void deveRejeitarDocumentoInvalido() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, "123")));

        verificarNenhumaInteracao();
    }

    @Test
    @DisplayName("Deve rejeitar cliente não encontrado")
    void deveRejeitarClienteNaoEncontrado() {
        CpfCnpj cpfCnpj = criarCpfCnpjValido();

        when(clienteRepository.buscarPorCpfCnpj(cpfCnpj)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, CPF_VALIDO)));

        verify(clienteRepository).buscarPorCpfCnpj(cpfCnpj);
        verifyNoInteractions(ordemServicoRepository, orcamentoRepository, estoqueRepository, historicoRepository, insumoPendenteRepository, codificadorCodigoAcesso);
    }

    @Test
    @DisplayName("Deve rejeitar ordem de serviço não encontrada")
    void deveRejeitarOrdemServicoNaoEncontrada() {
        CpfCnpj cpfCnpj = criarCpfCnpjValido();

        when(clienteRepository.buscarPorCpfCnpj(cpfCnpj)).thenReturn(Optional.of(cliente));
        when(ordemServicoRepository.buscarPoNumero(NUMERO_ORDEM_SERVICO)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, CPF_VALIDO)));

        verify(clienteRepository).buscarPorCpfCnpj(cpfCnpj);
        verify(ordemServicoRepository).buscarPoNumero(NUMERO_ORDEM_SERVICO);
        verifyNoInteractions(orcamentoRepository, estoqueRepository, historicoRepository, insumoPendenteRepository, codificadorCodigoAcesso);
    }

    @Test
    @DisplayName("Deve rejeitar quando a OS pertencer a outro cliente")
    void deveRejeitarOrdemServicoDeOutroCliente() {
        CpfCnpj cpfCnpj = criarCpfCnpjValido();

        when(clienteRepository.buscarPorCpfCnpj(cpfCnpj)).thenReturn(Optional.of(cliente));
        when(ordemServicoRepository.buscarPoNumero(NUMERO_ORDEM_SERVICO)).thenReturn(Optional.of(ordemServico));
        when(cliente.getId()).thenReturn(CLIENTE_ID);
        when(ordemServico.getClienteId()).thenReturn(ORDEM_SERVICO_ID);

        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, CPF_VALIDO)));

        verify(clienteRepository).buscarPorCpfCnpj(cpfCnpj);
        verify(ordemServicoRepository).buscarPoNumero(NUMERO_ORDEM_SERVICO);
        verifyNoInteractions(orcamentoRepository, estoqueRepository, historicoRepository, insumoPendenteRepository, codificadorCodigoAcesso);
    }

    @Test
    @DisplayName("Deve rejeitar código de acesso incorreto")
    void deveRejeitarCodigoAcessoIncorreto() {
        CpfCnpj cpfCnpj = criarCpfCnpjValido();

        when(clienteRepository.buscarPorCpfCnpj(cpfCnpj)).thenReturn(Optional.of(cliente));
        when(ordemServicoRepository.buscarPoNumero(NUMERO_ORDEM_SERVICO)).thenReturn(Optional.of(ordemServico));
        when(cliente.getId()).thenReturn(CLIENTE_ID);
        when(ordemServico.getClienteId()).thenReturn(CLIENTE_ID);
        when(codificadorCodigoAcesso.codificar(CODIGO_VERIFICACAO)).thenReturn("hash-diferente");
        when(ordemServico.getCodigoAcessoHash()).thenReturn(CODIGO_HASH);

        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, CPF_VALIDO)));

        verify(clienteRepository).buscarPorCpfCnpj(cpfCnpj);
        verify(ordemServicoRepository).buscarPoNumero(NUMERO_ORDEM_SERVICO);
        verifyNoInteractions(orcamentoRepository, estoqueRepository, historicoRepository, insumoPendenteRepository);
    }

    @Test
    @DisplayName("Deve rejeitar quando o orçamento não for encontrado")
    void deveRejeitarOrcamentoNaoEncontrado() {
        CpfCnpj cpfCnpj = criarCpfCnpjValido();

        when(clienteRepository.buscarPorCpfCnpj(cpfCnpj)).thenReturn(Optional.of(cliente));
        when(ordemServicoRepository.buscarPoNumero(NUMERO_ORDEM_SERVICO)).thenReturn(Optional.of(ordemServico));
        when(cliente.getId()).thenReturn(CLIENTE_ID);
        when(ordemServico.getClienteId()).thenReturn(CLIENTE_ID);
        when(codificadorCodigoAcesso.codificar(CODIGO_VERIFICACAO)).thenReturn(CODIGO_HASH);
        when(ordemServico.getCodigoAcessoHash()).thenReturn(CODIGO_HASH);
        when(ordemServico.getId()).thenReturn(ORDEM_SERVICO_ID);
        when(orcamentoRepository.buscarPorOrdemServicoId(ORDEM_SERVICO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(criarInput(NUMERO_ORDEM_SERVICO, CODIGO_VERIFICACAO, CPF_VALIDO)));

        verify(orcamentoRepository).buscarPorOrdemServicoId(ORDEM_SERVICO_ID);
        verify(orcamentoRepository, never()).salvar(any());
        verifyNoInteractions(estoqueRepository, historicoRepository, insumoPendenteRepository);
    }

    private CpfCnpj criarCpfCnpjValido() {
        return new CpfCnpj(TipoPessoa.PESSOA_FISICA, CPF_VALIDO);
    }

    private AprovarOrdemServicoInput criarInput(String numeroOrdemServico, String codigoVerificacao, String cpfCnpj) {

        return new AprovarOrdemServicoInput(numeroOrdemServico, codigoVerificacao, cpfCnpj);
    }

    private void verificarNenhumaInteracao() {
        verifyNoInteractions(clienteRepository, ordemServicoRepository, orcamentoRepository, estoqueRepository, historicoRepository, insumoPendenteRepository, codificadorCodigoAcesso);
    }
}