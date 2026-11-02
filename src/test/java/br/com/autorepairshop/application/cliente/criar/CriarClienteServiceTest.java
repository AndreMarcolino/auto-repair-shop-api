package br.com.autorepairshop.application.cliente.criar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarClienteServiceTest {

    private static final String NOME = "Cliente de Teste";
    private static final String CPF = "52998224725";
    private static final String CNPJ = "11222333000181";
    private static final String TELEFONE = "11999999999";
    private static final String EMAIL = "cliente@oficina.com.br";

    @Mock
    private ClienteRepository clienteRepository;

    @Captor
    private ArgumentCaptor<Cliente> clienteCaptor;

    @InjectMocks
    private CriarClienteService service;

    @Test
    @DisplayName("Deve criar cliente com CPF disponível e retornar seu ID")
    void deveCriarClientePessoaFisica() {
        CriarClienteInput input = criarInput(
                TipoPessoa.PESSOA_FISICA.getCodigo(), CPF);

        CpfCnpj cpfCnpj = CpfCnpj.criar(
                TipoPessoa.PESSOA_FISICA, CPF);

        when(clienteRepository.existeCpfCnpj(cpfCnpj))
                .thenReturn(false);

        CriarClienteOutput resultado = service.executar(input);

        verify(clienteRepository).existeCpfCnpj(cpfCnpj);
        verify(clienteRepository).salvar(clienteCaptor.capture());

        Cliente clienteSalvo = clienteCaptor.getValue();

        assertNotNull(resultado);
        assertNotNull(resultado.id());
        assertEquals(clienteSalvo.getId(), resultado.id());
        assertEquals(cpfCnpj, clienteSalvo.getCpfCnpj());
    }

    @Test
    @DisplayName("Deve criar cliente com CNPJ disponível e retornar seu ID")
    void deveCriarClientePessoaJuridica() {
        CriarClienteInput input = criarInput(
                TipoPessoa.PESSOA_JURIDICA.getCodigo(), CNPJ);

        CpfCnpj cpfCnpj = CpfCnpj.criar(
                TipoPessoa.PESSOA_JURIDICA, CNPJ);

        when(clienteRepository.existeCpfCnpj(cpfCnpj))
                .thenReturn(false);

        CriarClienteOutput resultado = service.executar(input);

        verify(clienteRepository).existeCpfCnpj(cpfCnpj);
        verify(clienteRepository).salvar(clienteCaptor.capture());

        Cliente clienteSalvo = clienteCaptor.getValue();

        assertNotNull(resultado);
        assertNotNull(resultado.id());
        assertEquals(clienteSalvo.getId(), resultado.id());
        assertEquals(cpfCnpj, clienteSalvo.getCpfCnpj());
    }

    @Test
    @DisplayName("Deve rejeitar CPF já cadastrado sem salvar cliente")
    void deveRejeitarCpfJaCadastrado() {
        CriarClienteInput input = criarInput(
                TipoPessoa.PESSOA_FISICA.getCodigo(), CPF);

        CpfCnpj cpfCnpj = CpfCnpj.criar(
                TipoPessoa.PESSOA_FISICA, CPF);

        when(clienteRepository.existeCpfCnpj(cpfCnpj))
                .thenReturn(true);

        assertThrows(
                DomainException.class,
                () -> service.executar(input)
        );

        verify(clienteRepository).existeCpfCnpj(cpfCnpj);
        verify(clienteRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ já cadastrado sem salvar cliente")
    void deveRejeitarCnpjJaCadastrado() {
        CriarClienteInput input = criarInput(
                TipoPessoa.PESSOA_JURIDICA.getCodigo(), CNPJ);

        CpfCnpj cpfCnpj = CpfCnpj.criar(
                TipoPessoa.PESSOA_JURIDICA, CNPJ);

        when(clienteRepository.existeCpfCnpj(cpfCnpj))
                .thenReturn(true);

        assertThrows(
                DomainException.class,
                () -> service.executar(input)
        );

        verify(clienteRepository).existeCpfCnpj(cpfCnpj);
        verify(clienteRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve rejeitar tipo de pessoa inválido sem acessar repositório")
    void deveRejeitarTipoPessoaInvalido() {
        CriarClienteInput input = criarInput((short) 99, CPF);

        assertThrows(
                DomainException.class,
                () -> service.executar(input)
        );

        verifyNoInteractions(clienteRepository);
    }

    @Test
    @DisplayName("Deve rejeitar tipo de pessoa nulo sem acessar repositório")
    void deveRejeitarTipoPessoaNulo() {
        CriarClienteInput input = criarInput(null, CPF);

        assertThrows(
                DomainException.class,
                () -> service.executar(input)
        );

        verifyNoInteractions(clienteRepository);
    }

    private CriarClienteInput criarInput(Short tipoPessoa, String documento) {
        return new CriarClienteInput(
                NOME,
                tipoPessoa,
                documento,
                TELEFONE,
                EMAIL
        );
    }
}