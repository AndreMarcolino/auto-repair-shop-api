package br.com.autorepairshop.application.ordemservico.abrirordemservico;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.veiculo.Veiculo;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;
import br.com.autorepairshop.infrastructure.security.CodificadorCodigoAcesso;
import br.com.autorepairshop.infrastructure.security.GeradorCodigoAcesso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbrirOrdemServicoServiceTest {

    private static final UUID CLIENTE_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");
    private static final UUID VEICULO_ID = UUID.fromString("b92e5ed1-a7ca-4b1d-8398-0e5d5c440029");

    private static final String CODIGO_ACESSO = "123456";
    private static final String CODIGO_ACESSO_HASH = "hash-do-codigo";
    private static final String PROBLEMA_RELATADO = "Ruído ao frear";

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private GeradorCodigoAcesso geradorCodigoAcesso;

    @Mock
    private CodificadorCodigoAcesso codificadorCodigoAcesso;

    @Mock
    private Cliente cliente;

    @Mock
    private Veiculo veiculo;

    @InjectMocks
    private AbrirOrdemServicoService service;

    @Test
    @DisplayName("Deve abrir uma ordem de serviço para cliente e veículo válidos")
    void deveAbrirOrdemServicoComSucesso() {
        AbrirOrdemServicoInput input = criarInput(CLIENTE_ID.toString(), VEICULO_ID.toString(), PROBLEMA_RELATADO);

        when(clienteRepository.buscarPorId(CLIENTE_ID)).thenReturn(Optional.of(cliente));
        when(cliente.isAtivo()).thenReturn(true);
        when(veiculoRepository.buscarPorId(VEICULO_ID)).thenReturn(Optional.of(veiculo));
        when(ordemServicoRepository.proximoNumero()).thenReturn(42L);
        when(geradorCodigoAcesso.gerar()).thenReturn(CODIGO_ACESSO);
        when(codificadorCodigoAcesso.codificar(CODIGO_ACESSO)).thenReturn(CODIGO_ACESSO_HASH);

        AbrirOrdemServicoOutput resultado = service.executar(input);

        String numeroEsperado = "OS-%d-%06d".formatted(java.time.Year.now().getValue(), 42L);

        assertNotNull(resultado);
        assertEquals(numeroEsperado, resultado.numero());
        assertEquals(CODIGO_ACESSO, resultado.codigoAcesso());

        ArgumentCaptor<OrdemServico> ordemServicoCaptor = ArgumentCaptor.forClass(OrdemServico.class);

        verify(ordemServicoRepository).salvar(ordemServicoCaptor.capture());
        assertNotNull(ordemServicoCaptor.getValue());

        verify(clienteRepository).buscarPorId(CLIENTE_ID);
        verify(veiculoRepository).buscarPorId(VEICULO_ID);
        verify(geradorCodigoAcesso).gerar();
        verify(codificadorCodigoAcesso).codificar(CODIGO_ACESSO);
    }

    @Test
    @DisplayName("Deve rejeitar ID de cliente nulo sem consultar repositórios")
    void deveRejeitarIdClienteNulo() {
        AbrirOrdemServicoInput input = criarInput(null, VEICULO_ID.toString(), PROBLEMA_RELATADO);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(clienteRepository, veiculoRepository, ordemServicoRepository, geradorCodigoAcesso, codificadorCodigoAcesso);
    }

    @Test
    @DisplayName("Deve rejeitar ID de cliente inválido sem consultar repositórios")
    void deveRejeitarIdClienteInvalido() {
        AbrirOrdemServicoInput input = criarInput("cliente-invalido", VEICULO_ID.toString(), PROBLEMA_RELATADO);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(clienteRepository, veiculoRepository, ordemServicoRepository, geradorCodigoAcesso, codificadorCodigoAcesso);
    }

    @Test
    @DisplayName("Deve rejeitar ID de veículo nulo sem consultar repositórios")
    void deveRejeitarIdVeiculoNulo() {
        AbrirOrdemServicoInput input = criarInput(CLIENTE_ID.toString(), null, PROBLEMA_RELATADO);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(clienteRepository, veiculoRepository, ordemServicoRepository, geradorCodigoAcesso, codificadorCodigoAcesso);
    }

    @Test
    @DisplayName("Deve rejeitar ID de veículo inválido sem consultar repositórios")
    void deveRejeitarIdVeiculoInvalido() {
        AbrirOrdemServicoInput input = criarInput(CLIENTE_ID.toString(), "veiculo-invalido", PROBLEMA_RELATADO);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(clienteRepository, veiculoRepository, ordemServicoRepository, geradorCodigoAcesso, codificadorCodigoAcesso);
    }

    @Test
    @DisplayName("Deve rejeitar cliente não encontrado")
    void deveRejeitarClienteNaoEncontrado() {
        AbrirOrdemServicoInput input = criarInput(CLIENTE_ID.toString(), VEICULO_ID.toString(), PROBLEMA_RELATADO);

        when(clienteRepository.buscarPorId(CLIENTE_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(input));

        verify(clienteRepository).buscarPorId(CLIENTE_ID);
        verifyNoInteractions(veiculoRepository, ordemServicoRepository, geradorCodigoAcesso, codificadorCodigoAcesso);
    }

    @Test
    @DisplayName("Deve rejeitar abertura para cliente desativado")
    void deveRejeitarClienteDesativado() {
        AbrirOrdemServicoInput input = criarInput(CLIENTE_ID.toString(), VEICULO_ID.toString(), PROBLEMA_RELATADO);

        when(clienteRepository.buscarPorId(CLIENTE_ID)).thenReturn(Optional.of(cliente));
        when(cliente.isAtivo()).thenReturn(false);

        assertThrows(DomainException.class, () -> service.executar(input));

        verify(clienteRepository).buscarPorId(CLIENTE_ID);
        verifyNoInteractions(veiculoRepository, ordemServicoRepository, geradorCodigoAcesso, codificadorCodigoAcesso);
    }

    @Test
    @DisplayName("Deve rejeitar veículo não encontrado")
    void deveRejeitarVeiculoNaoEncontrado() {
        AbrirOrdemServicoInput input = criarInput(CLIENTE_ID.toString(), VEICULO_ID.toString(), PROBLEMA_RELATADO);

        when(clienteRepository.buscarPorId(CLIENTE_ID)).thenReturn(Optional.of(cliente));
        when(cliente.isAtivo()).thenReturn(true);
        when(veiculoRepository.buscarPorId(VEICULO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(input));

        verify(veiculoRepository).buscarPorId(VEICULO_ID);
        verifyNoInteractions(ordemServicoRepository, geradorCodigoAcesso, codificadorCodigoAcesso);
    }

    private AbrirOrdemServicoInput criarInput(String idCliente, String idVeiculo, String problemaRelatado) {

        return new AbrirOrdemServicoInput(idCliente, idVeiculo, problemaRelatado);
    }
}