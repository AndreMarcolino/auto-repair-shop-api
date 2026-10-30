package br.com.autorepairshop.application.cliente.buscarclientecpfcnpj;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.application.cliente.shared.ClienteOutput;
import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarClientePorCpfCnpjServiceTest {

    private static final String CPF = "52998224725";

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private Cliente cliente;

    @Mock
    private ClienteOutput clienteOutput;

    @InjectMocks
    private BuscarClientePorCpfCnpjService service;

    @Test
    @DisplayName("Deve retornar cliente convertido quando o CPF existir")
    void deveRetornarClienteQuandoEncontrado() {
        CpfCnpj cpfCnpj = CpfCnpj.criar(
                TipoPessoa.PESSOA_FISICA, CPF);

        when(clienteRepository.buscarPorCpfCnpj(cpfCnpj))
                .thenReturn(Optional.of(cliente));

        try (MockedStatic<ClienteOutput> conversao =
                     mockStatic(ClienteOutput.class)) {

            conversao.when(() -> ClienteOutput.fromDomain(cliente))
                    .thenReturn(clienteOutput);

            Optional<ClienteOutput> resultado = service.executar(
                    TipoPessoa.PESSOA_FISICA.getCodigo(), CPF);

            assertTrue(resultado.isPresent());
            assertSame(clienteOutput, resultado.orElseThrow());

            verify(clienteRepository).buscarPorCpfCnpj(cpfCnpj);
            conversao.verify(() -> ClienteOutput.fromDomain(cliente));
        }
    }

    @Test
    @DisplayName("Deve retornar vazio sem converter quando o cliente não existir")
    void deveRetornarVazioQuandoClienteNaoEncontrado() {
        CpfCnpj cpfCnpj = CpfCnpj.criar(
                TipoPessoa.PESSOA_FISICA, CPF);

        when(clienteRepository.buscarPorCpfCnpj(cpfCnpj))
                .thenReturn(Optional.empty());

        try (MockedStatic<ClienteOutput> conversao =
                     mockStatic(ClienteOutput.class)) {

            Optional<ClienteOutput> resultado = service.executar(
                    TipoPessoa.PESSOA_FISICA.getCodigo(), CPF);

            assertTrue(resultado.isEmpty());

            verify(clienteRepository).buscarPorCpfCnpj(cpfCnpj);
            conversao.verifyNoInteractions();
        }
    }

    @Test
    @DisplayName("Deve rejeitar tipo de pessoa inválido sem consultar repositório")
    void deveRejeitarTipoPessoaInvalido() {
        assertThrows(
                DomainException.class,
                () -> service.executar((short) 99, CPF)
        );

        verifyNoInteractions(clienteRepository);
    }

    @Test
    @DisplayName("Deve rejeitar tipo de pessoa nulo sem consultar repositório")
    void deveRejeitarTipoPessoaNulo() {
        assertThrows(
                DomainException.class,
                () -> service.executar(null, CPF)
        );

        verifyNoInteractions(clienteRepository);
    }
}