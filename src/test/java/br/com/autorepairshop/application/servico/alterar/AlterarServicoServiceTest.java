package br.com.autorepairshop.application.servico.alterar;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.servico.Servico;
import br.com.autorepairshop.domain.servico.ServicoRepository;
import br.com.autorepairshop.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlterarServicoServiceTest {

    private static final UUID SERVICO_ID = UUID.fromString("a82e5ed1-a7ca-4b1d-8398-0e5d5c440028");

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private Servico servico;

    @InjectMocks
    private AlterarServicoService service;

    @Test
    @DisplayName("Deve atualizar os dados do serviço e salvá-lo")
    void deveAtualizarServico() {
        String descricao = "Troca de óleo";
        BigDecimal valorReferencia = new BigDecimal("150.00");

        AlterarServicoInput input = new AlterarServicoInput(SERVICO_ID.toString(), descricao, valorReferencia);

        when(servicoRepository.buscarPorId(SERVICO_ID)).thenReturn(Optional.of(servico));

        service.executar(input);

        verify(servicoRepository).buscarPorId(SERVICO_ID);
        verify(servico).atualizarDados(descricao, valorReferencia);
        verify(servicoRepository).salvar(servico);
        verifyNoMoreInteractions(servicoRepository, servico);
    }

    @Test
    @DisplayName("Deve rejeitar ID nulo")
    void deveRejeitarIdNulo() {
        AlterarServicoInput input = new AlterarServicoInput(null, "Troca de óleo", new BigDecimal("150.00"));

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(servicoRepository, servico);
    }

    @Test
    @DisplayName("Deve rejeitar ID inválido")
    void deveRejeitarIdInvalido() {
        AlterarServicoInput input = new AlterarServicoInput("id-invalido", "Troca de óleo", new BigDecimal("150.00"));

        assertThrows(DomainException.class, () -> service.executar(input));

        verifyNoInteractions(servicoRepository, servico);
    }

    @Test
    @DisplayName("Deve rejeitar serviço não encontrado")
    void deveRejeitarServicoNaoEncontrado() {
        AlterarServicoInput input = new AlterarServicoInput(SERVICO_ID.toString(), "Troca de óleo", new BigDecimal("150.00"));

        when(servicoRepository.buscarPorId(SERVICO_ID)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> service.executar(input));

        verify(servicoRepository).buscarPorId(SERVICO_ID);
        verify(servicoRepository, never()).salvar(any());
        verifyNoInteractions(servico);
    }
}