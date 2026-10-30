package br.com.autorepairshop.application.autenticacao;

import br.com.autorepairshop.domain.funcionario.Funcionario;
import br.com.autorepairshop.domain.funcionario.FuncionarioRepository;
import br.com.autorepairshop.domain.shared.CredenciaisInvalidasException;
import br.com.autorepairshop.infrastructure.security.JwtService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticarFuncionarioServiceTest {

    private static final String EMAIL = "atendente@oficina.com.br";
    private static final String SENHA = "senha-de-teste";
    private static final String SENHA_HASH = "hash-de-teste";
    private static final String TOKEN = "token-de-teste";

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private Funcionario funcionario;

    @InjectMocks
    private AutenticarFuncionarioService service;

    @Test
    @DisplayName("Deve retornar token Bearer quando as credenciais forem válidas")
    void deveAutenticarFuncionarioComCredenciaisValidas() {
        // Arrange
        LoginInput request = new LoginInput(EMAIL, SENHA);

        when(funcionarioRepository.buscarPorEmail(EMAIL)).thenReturn(Optional.of(funcionario));
        when(funcionario.isAtivo()).thenReturn(true);
        when(funcionario.getSenhaHash()).thenReturn(SENHA_HASH);
        when(passwordEncoder.matches(SENHA, SENHA_HASH)).thenReturn(true);
        when(jwtService.gerarToken(funcionario)).thenReturn(TOKEN);

        // Act
        LoginOutput resultado = service.executar(request);

        // Assert
        assertEquals(new LoginOutput(TOKEN, "Bearer", 3600), resultado);

        verify(funcionarioRepository).buscarPorEmail(EMAIL);
        verify(passwordEncoder).matches(SENHA, SENHA_HASH);
        verify(jwtService).gerarToken(funcionario);
    }

    @Test
    @DisplayName("Deve rejeitar autenticação quando o funcionário não existir")
    void deveRejeitarFuncionarioNaoEncontrado() {
        // Arrange
        LoginInput request = new LoginInput(EMAIL, SENHA);

        when(funcionarioRepository.buscarPorEmail(EMAIL)).thenReturn(Optional.empty());

        // Act e Assert
        assertThrows(CredenciaisInvalidasException.class, () -> service.executar(request));

        verify(funcionarioRepository).buscarPorEmail(EMAIL);
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    @DisplayName("Deve rejeitar funcionário inativo sem verificar a senha")
    void deveRejeitarFuncionarioInativo() {
        // Arrange
        LoginInput request = new LoginInput(EMAIL, SENHA);

        when(funcionarioRepository.buscarPorEmail(EMAIL)).thenReturn(Optional.of(funcionario));
        when(funcionario.isAtivo()).thenReturn(false);

        // Act e Assert
        assertThrows(CredenciaisInvalidasException.class, () -> service.executar(request));

        verify(funcionarioRepository).buscarPorEmail(EMAIL);
        verify(funcionario).isAtivo();
        verify(funcionario, never()).getSenhaHash();
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    @DisplayName("Deve rejeitar senha incorreta sem gerar token")
    void deveRejeitarSenhaIncorreta() {
        // Arrange
        LoginInput request = new LoginInput(EMAIL, SENHA);

        when(funcionarioRepository.buscarPorEmail(EMAIL)).thenReturn(Optional.of(funcionario));
        when(funcionario.isAtivo()).thenReturn(true);
        when(funcionario.getSenhaHash()).thenReturn(SENHA_HASH);
        when(passwordEncoder.matches(SENHA, SENHA_HASH)).thenReturn(false);

        // Act e Assert
        assertThrows(CredenciaisInvalidasException.class, () -> service.executar(request));

        verify(funcionarioRepository).buscarPorEmail(EMAIL);
        verify(passwordEncoder).matches(SENHA, SENHA_HASH);
        verifyNoInteractions(jwtService);
    }
}