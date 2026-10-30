package br.com.autorepairshop.application.servico.criar;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.autorepairshop.domain.servico.Servico;
import br.com.autorepairshop.domain.servico.ServicoRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarServicoServiceTest {

    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private CriarServicoService service;

    @Test
    @DisplayName("Deve criar, salvar e retornar o serviço")
    void deveCriarSalvarERetornarServico() {
        String descricao = "Troca de óleo";
        BigDecimal valorReferencia = new BigDecimal("150.00");

        CriarServicoInput input = new CriarServicoInput(descricao, valorReferencia);

        when(servicoRepository.salvar(any(Servico.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        CriarServicoOutput resultado = service.executar(input);

        ArgumentCaptor<Servico> captor = ArgumentCaptor.forClass(Servico.class);

        verify(servicoRepository).salvar(captor.capture());

        Servico servicoSalvo = captor.getValue();
        assertNotNull(servicoSalvo);
        assertEquals(descricao, servicoSalvo.getDescricao());
        assertEquals(valorReferencia, servicoSalvo.getValorReferencia());

        assertNotNull(resultado);
        assertEquals(CriarServicoOutput.fromDomain(servicoSalvo), resultado);

        verifyNoMoreInteractions(servicoRepository);
    }
}