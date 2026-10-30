package br.com.autorepairshop.application.ordemservico.iniciarservico;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.funcionario.Funcionario;
import br.com.autorepairshop.domain.funcionario.FuncionarioRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IniciarServicoOrdemServicoServiceTest {

    private static final UUID ORDEM_SERVICO_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    private static final UUID MECANICO_ID = UUID.fromString("b92e5ed1-a7ca-4b1d-8398-0e5d5c440029");

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private HistoricoStatusOrdemServicoRepository historicoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private Funcionario funcionario;

    @Mock
    private OrdemServico ordemServico;

    @Mock
    private HistoricoStatusOrdemServico historico;

    @InjectMocks
    private IniciarServicoOrdemServicoService service;

    @Test
    @DisplayName("Deve iniciar o serviço, salvar a OS e registrar o histórico")
    void deveIniciarServicoESalvarHistorico() {
        IniciarServicoOrdemServicoInput input = criarInput(ORDEM_SERVICO_ID.toString(), MECANICO_ID.toString());

        when(funcionarioRepository.buscarPorId(MECANICO_ID)).thenReturn(Optional.of(funcionario));
        when(funcionario.podeAtenderExecutarServico()).thenReturn(true);
        when(ordemServicoRepository.buscarPorId(ORDEM_SERVICO_ID)).thenReturn(Optional.of(ordemServico));

        try (MockedStatic<HistoricoStatusOrdemServico> historicoStatic = mockStatic(HistoricoStatusOrdemServico.class)) {

            historicoStatic.when(() -> HistoricoStatusOrdemServico.registrar(ORDEM_SERVICO_ID, null, null)).thenReturn(historico);

            service.executar(input);

            InOrder ordem = inOrder(funcionarioRepository, ordemServicoRepository, ordemServico, historicoRepository);

            ordem.verify(funcionarioRepository).buscarPorId(MECANICO_ID);
            ordem.verify(ordemServicoRepository).buscarPorId(ORDEM_SERVICO_ID);
            ordem.verify(ordemServico).iniciarServico();
            ordem.verify(ordemServicoRepository).salvar(ordemServico);
            ordem.verify(historicoRepository).salvar(historico);

            historicoStatic.verify(() -> HistoricoStatusOrdemServico.registrar(ORDEM_SERVICO_ID, null, null));
        }
    }

    @Test
    @DisplayName("Deve rejeitar ID da OS nulo")
    void deveRejeitarIdOrdemServicoNulo() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(null, MECANICO_ID.toString())));

        verificarNenhumaGravacao();
    }

    @Test
    @DisplayName("Deve rejeitar ID da OS em branco")
    void deveRejeitarIdOrdemServicoEmBranco() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(" ", MECANICO_ID.toString())));

        verificarNenhumaGravacao();
    }

    @Test
    @DisplayName("Deve rejeitar ID da OS inválido")
    void deveRejeitarIdOrdemServicoInvalido() {
        assertThrows(DomainException.class, () -> service.executar(criarInput("os-invalida", MECANICO_ID.toString())));

        verificarNenhumaGravacao();
    }

    @Test
    @DisplayName("Deve rejeitar ID do mecânico nulo")
    void deveRejeitarIdMecanicoNulo() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), null)));

        verificarNenhumaGravacao();
    }

    @Test
    @DisplayName("Deve rejeitar ID do mecânico em branco")
    void deveRejeitarIdMecanicoEmBranco() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), " ")));

        verificarNenhumaGravacao();
    }

    @Test
    @DisplayName("Deve rejeitar ID do mecânico inválido")
    void deveRejeitarIdMecanicoInvalido() {
        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), "mecanico-invalido")));

        verificarNenhumaGravacao();
    }

    @Test
    @DisplayName("Deve rejeitar mecânico não encontrado")
    void deveRejeitarMecanicoNaoEncontrado() {
        when(funcionarioRepository.buscarPorId(MECANICO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), MECANICO_ID.toString())));

        verify(funcionarioRepository).buscarPorId(MECANICO_ID);
        verifyNoInteractions(ordemServicoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar mecânico sem permissão")
    void deveRejeitarMecanicoSemPermissao() {
        when(funcionarioRepository.buscarPorId(MECANICO_ID)).thenReturn(Optional.of(funcionario));
        when(funcionario.podeAtenderExecutarServico()).thenReturn(false);

        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), MECANICO_ID.toString())));

        verifyNoInteractions(ordemServicoRepository, historicoRepository);
    }

    @Test
    @DisplayName("Deve rejeitar OS não encontrada")
    void deveRejeitarOrdemServicoNaoEncontrada() {
        when(funcionarioRepository.buscarPorId(MECANICO_ID)).thenReturn(Optional.of(funcionario));
        when(funcionario.podeAtenderExecutarServico()).thenReturn(true);
        when(ordemServicoRepository.buscarPorId(ORDEM_SERVICO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(criarInput(ORDEM_SERVICO_ID.toString(), MECANICO_ID.toString())));

        verify(ordemServicoRepository).buscarPorId(ORDEM_SERVICO_ID);
        verify(ordemServicoRepository, never()).salvar(any());
        verifyNoInteractions(historicoRepository);
    }

    private void verificarNenhumaGravacao() {
        verifyNoInteractions(funcionarioRepository, ordemServicoRepository, historicoRepository);
    }

    private IniciarServicoOrdemServicoInput criarInput(String idOrdemServico, String mecanicoId) {

        return new IniciarServicoOrdemServicoInput(idOrdemServico, mecanicoId);
    }
}