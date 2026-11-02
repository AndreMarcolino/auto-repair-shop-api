package br.com.autorepairshop.application.cliente.listar;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.application.cliente.shared.ClienteOutput;
import br.com.autorepairshop.application.shared.dto.PaginacaoInput;
import br.com.autorepairshop.application.shared.dto.PaginacaoOutput;
import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.shared.Pagina;
import br.com.autorepairshop.domain.shared.Paginacao;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.Email;
import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarClientesServiceTest {

    private static final int NUMERO_PAGINA = 0;
    private static final int TAMANHO_PAGINA = 10;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private Cliente cliente;

    @Mock
    private ClienteOutput clienteOutput;

    @Mock
    private PaginacaoOutput paginacaoOutput;

    @InjectMocks
    private ListarClientesService service;

    @Test
    @DisplayName("Deve listar clientes sem filtros opcionais")
    void deveListarClientesSemFiltrosOpcionais() {
        ListarClientesInput input = new ListarClientesInput(
                null, null, null, null, null, null);
        PaginacaoInput paginacaoInput =
                new PaginacaoInput(NUMERO_PAGINA, TAMANHO_PAGINA);

        Pagina<Cliente> pagina = criarPagina(List.of(cliente));
        when(clienteRepository.buscar(
                null, null, null, null, null, null, paginacaoInput.toPaginacao()))
                .thenReturn(pagina);

        try (MockedStatic<ClienteOutput> conversaoCliente =
                     mockStatic(ClienteOutput.class);
             MockedStatic<PaginacaoOutput> conversaoPaginacao =
                     mockStatic(PaginacaoOutput.class)) {

            conversaoCliente.when(() -> ClienteOutput.fromDomain(cliente))
                    .thenReturn(clienteOutput);
            conversaoPaginacao.when(() -> PaginacaoOutput.de(pagina))
                    .thenReturn(paginacaoOutput);

            ListarClientesOutput resultado =
                    service.executar(input, paginacaoInput);

            assertEquals(List.of(clienteOutput), resultado.clientes());
            assertSame(paginacaoOutput, resultado.paginacao());

            verify(clienteRepository).buscar(
                    null, null, null, null, null, null,
                    paginacaoInput.toPaginacao());
            conversaoCliente.verify(() -> ClienteOutput.fromDomain(cliente));
            conversaoPaginacao.verify(() -> PaginacaoOutput.de(pagina));
        }
    }

    @Test
    @DisplayName("Deve encaminhar os filtros válidos ao repositório")
    void deveListarClientesComFiltros() {
        String nome = "Cliente Teste";
        String cpf = "52998224725";
        String emailInformado = "cliente@oficina.com.br";
        String telefone = "11999999999";
        Boolean ativo = true;

        ListarClientesInput input = new ListarClientesInput(
                nome,
                TipoPessoa.PESSOA_FISICA.getCodigo(),
                cpf,
                emailInformado,
                ativo,
                telefone
        );
        PaginacaoInput paginacaoInput =
                new PaginacaoInput(NUMERO_PAGINA, TAMANHO_PAGINA);

        CpfCnpj cpfCnpj = CpfCnpj.criar(TipoPessoa.PESSOA_FISICA, cpf);
        Email email = Email.criar(emailInformado);
        Pagina<Cliente> pagina = criarPagina(List.of(cliente));

        when(clienteRepository.buscar(
                nome,
                TipoPessoa.PESSOA_FISICA.getCodigo(),
                cpfCnpj.cpfCnpj(),
                email.email(),
                telefone,
                ativo,
                paginacaoInput.toPaginacao()))
                .thenReturn(pagina);

        try (MockedStatic<ClienteOutput> conversaoCliente =
                     mockStatic(ClienteOutput.class);
             MockedStatic<PaginacaoOutput> conversaoPaginacao =
                     mockStatic(PaginacaoOutput.class)) {

            conversaoCliente.when(() -> ClienteOutput.fromDomain(cliente))
                    .thenReturn(clienteOutput);
            conversaoPaginacao.when(() -> PaginacaoOutput.de(pagina))
                    .thenReturn(paginacaoOutput);

            ListarClientesOutput resultado =
                    service.executar(input, paginacaoInput);

            assertEquals(List.of(clienteOutput), resultado.clientes());
            assertSame(paginacaoOutput, resultado.paginacao());

            verify(clienteRepository).buscar(
                    nome,
                    TipoPessoa.PESSOA_FISICA.getCodigo(),
                    cpfCnpj.cpfCnpj(),
                    email.email(),
                    telefone,
                    ativo,
                    paginacaoInput.toPaginacao());
        }
    }

    @Test
    @DisplayName("Deve ignorar e-mail em branco")
    void deveIgnorarEmailEmBranco() {
        ListarClientesInput input = new ListarClientesInput(
                null, null, null, "   ", null, null);
        PaginacaoInput paginacaoInput =
                new PaginacaoInput(NUMERO_PAGINA, TAMANHO_PAGINA);

        Pagina<Cliente> pagina = criarPagina(List.of());

        when(clienteRepository.buscar(
                null, null, null, null, null, null, paginacaoInput.toPaginacao()))
                .thenReturn(pagina);

        try (MockedStatic<PaginacaoOutput> conversaoPaginacao =
                     mockStatic(PaginacaoOutput.class)) {

            conversaoPaginacao.when(() -> PaginacaoOutput.de(pagina))
                    .thenReturn(paginacaoOutput);

            ListarClientesOutput resultado =
                    service.executar(input, paginacaoInput);

            assertTrue(resultado.clientes().isEmpty());
            assertSame(paginacaoOutput, resultado.paginacao());

            verify(clienteRepository).buscar(
                    null, null, null, null, null, null,
                    paginacaoInput.toPaginacao());
        }
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver clientes")
    void deveRetornarListaVazia() {
        ListarClientesInput input = new ListarClientesInput(
                null, null, null, null, null, null);
        PaginacaoInput paginacaoInput =
                new PaginacaoInput(NUMERO_PAGINA, TAMANHO_PAGINA);

        Pagina<Cliente> pagina = criarPagina(List.of());

        when(clienteRepository.buscar(
                null, null, null, null, null, null, paginacaoInput.toPaginacao()))
                .thenReturn(pagina);

        try (MockedStatic<PaginacaoOutput> conversaoPaginacao =
                     mockStatic(PaginacaoOutput.class)) {

            conversaoPaginacao.when(() -> PaginacaoOutput.de(pagina))
                    .thenReturn(paginacaoOutput);

            ListarClientesOutput resultado =
                    service.executar(input, paginacaoInput);

            assertNotNull(resultado);
            assertTrue(resultado.clientes().isEmpty());
            assertSame(paginacaoOutput, resultado.paginacao());

            verify(clienteRepository).buscar(
                    null, null, null, null, null, null,
                    paginacaoInput.toPaginacao());
            conversaoPaginacao.verify(() -> PaginacaoOutput.de(pagina));
        }
    }

    @Test
    @DisplayName("Deve rejeitar tipo de pessoa inválido sem consultar repositório")
    void deveRejeitarTipoPessoaInvalido() {
        ListarClientesInput input = new ListarClientesInput(
                null, (short) 99, null, null, null, null);
        PaginacaoInput paginacaoInput =
                new PaginacaoInput(NUMERO_PAGINA, TAMANHO_PAGINA);

        assertThrows(
                DomainException.class,
                () -> service.executar(input, paginacaoInput)
        );

        verifyNoInteractions(clienteRepository);
    }

    private Pagina<Cliente> criarPagina(List<Cliente> clientes) {
        return new Pagina<>(
                clientes,
                NUMERO_PAGINA,
                TAMANHO_PAGINA,
                clientes.size(),
                clientes.isEmpty() ? 0 : 1
        );
    }
}