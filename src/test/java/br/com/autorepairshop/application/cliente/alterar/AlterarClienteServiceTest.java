package br.com.autorepairshop.application.cliente.alterar;

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
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.Email;
import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlterarClienteServiceTest {

    private static final UUID ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    private static final String NOME = "Cliente de Teste";
    private static final String CPF_ATUAL = "52998224725";
    private static final String CPF_NOVO = "11144477735";
    private static final String TELEFONE = "11999999999";
    private static final String EMAIL = "cliente@oficina.com.br";

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private Cliente cliente;

    @InjectMocks
    private AlterarClienteService service;

    @Test
    @DisplayName("Deve atualizar e salvar cliente com novo CPF disponível")
    void deveAlterarClienteComNovoCpfDisponivel() {
        AlterarClienteInput input = criarInput(CPF_NOVO);
        CpfCnpj cpfAtual = criarCpf(CPF_ATUAL);
        CpfCnpj cpfNovo = criarCpf(CPF_NOVO);
        Email email = Email.criar(EMAIL);

        when(clienteRepository.buscarPorId(ID)).thenReturn(Optional.of(cliente));
        when(cliente.getCpfCnpj()).thenReturn(cpfAtual);
        when(clienteRepository.existeCpfCnpj(cpfNovo)).thenReturn(false);

        service.executar(input);

        verify(clienteRepository).buscarPorId(ID);
        verify(clienteRepository).existeCpfCnpj(cpfNovo);
        verify(cliente).atualizarCadastro(NOME, cpfNovo, TELEFONE, email);
        verify(clienteRepository).salvar(cliente);
    }

    @Test
    @DisplayName("Deve manter CPF sem consultar duplicidade")
    void deveAlterarClienteMantendoCpfAtual() {
        AlterarClienteInput input = criarInput(CPF_ATUAL);
        CpfCnpj cpfAtual = criarCpf(CPF_ATUAL);
        Email email = Email.criar(EMAIL);

        when(clienteRepository.buscarPorId(ID)).thenReturn(Optional.of(cliente));
        when(cliente.getCpfCnpj()).thenReturn(cpfAtual);

        service.executar(input);

        verify(clienteRepository, never()).existeCpfCnpj(any(CpfCnpj.class));
        verify(cliente).atualizarCadastro(NOME, cpfAtual, TELEFONE, email);
        verify(clienteRepository).salvar(cliente);
    }

    @Test
    @DisplayName("Deve rejeitar ID com formato inválido")
    void deveRejeitarIdInvalido() {
        AlterarClienteInput input = new AlterarClienteInput("id-invalido", NOME, TipoPessoa.PESSOA_FISICA.getCodigo(), CPF_ATUAL, TELEFONE, EMAIL);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(clienteRepository, cliente);
    }

    @Test
    @DisplayName("Deve rejeitar ID nulo")
    void deveRejeitarIdNulo() {
        AlterarClienteInput input = new AlterarClienteInput(null, NOME, TipoPessoa.PESSOA_FISICA.getCodigo(), CPF_ATUAL, TELEFONE, EMAIL);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(clienteRepository, cliente);
    }

    @Test
    @DisplayName("Deve rejeitar cliente não encontrado")
    void deveRejeitarClienteNaoEncontrado() {
        AlterarClienteInput input = criarInput(CPF_ATUAL);

        when(clienteRepository.buscarPorId(ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(input));

        verify(clienteRepository).buscarPorId(ID);
        verify(clienteRepository, never()).existeCpfCnpj(any(CpfCnpj.class));
        verify(clienteRepository, never()).salvar(any());
        verifyNoInteractions(cliente);
    }

    @Test
    @DisplayName("Deve rejeitar novo CPF já cadastrado")
    void deveRejeitarCpfJaCadastrado() {
        AlterarClienteInput input = criarInput(CPF_NOVO);
        CpfCnpj cpfAtual = criarCpf(CPF_ATUAL);
        CpfCnpj cpfNovo = criarCpf(CPF_NOVO);

        when(clienteRepository.buscarPorId(ID)).thenReturn(Optional.of(cliente));
        when(cliente.getCpfCnpj()).thenReturn(cpfAtual);
        when(clienteRepository.existeCpfCnpj(cpfNovo)).thenReturn(true);

        assertThrows(DomainException.class, () -> service.executar(input));

        verify(clienteRepository).existeCpfCnpj(cpfNovo);
        verify(cliente, never()).atualizarCadastro(any(), any(), any(), any());
        verify(clienteRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve rejeitar código de tipo de pessoa inválido")
    void deveRejeitarTipoPessoaInvalido() {
        AlterarClienteInput input = new AlterarClienteInput(ID.toString(), NOME, (short) 99, CPF_ATUAL, TELEFONE, EMAIL);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(clienteRepository, cliente);
    }

    @Test
    @DisplayName("Deve rejeitar tipo de pessoa nulo")
    void deveRejeitarTipoPessoaNulo() {
        AlterarClienteInput input = new AlterarClienteInput(ID.toString(), NOME, null, CPF_ATUAL, TELEFONE, EMAIL);

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(clienteRepository, cliente);
    }

    private AlterarClienteInput criarInput(String cpf) {
        return new AlterarClienteInput(ID.toString(), NOME, TipoPessoa.PESSOA_FISICA.getCodigo(), cpf, TELEFONE, EMAIL);
    }

    private CpfCnpj criarCpf(String cpf) {
        return CpfCnpj.criar(TipoPessoa.PESSOA_FISICA, cpf);
    }
}