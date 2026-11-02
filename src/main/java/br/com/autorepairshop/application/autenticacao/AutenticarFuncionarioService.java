package br.com.autorepairshop.application.autenticacao;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.autorepairshop.domain.funcionario.Funcionario;
import br.com.autorepairshop.domain.funcionario.FuncionarioRepository;
import br.com.autorepairshop.domain.shared.CredenciaisInvalidasException;
import br.com.autorepairshop.domain.shared.valueobject.Email;
import br.com.autorepairshop.infrastructure.security.JwtService;

@Service
public class AutenticarFuncionarioService implements AutenticarFuncionarioUseCase {

    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AutenticarFuncionarioService (
            FuncionarioRepository funcionarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginOutput executar(LoginInput request) {
    	Email.criar(request.email());
        
    	Funcionario funcionario = funcionarioRepository
                .buscarPorEmail(request.email())
                .filter(Funcionario::isAtivo)
                .filter(f -> passwordEncoder.matches(
                        request.senha(),
                        f.getSenhaHash()))
                .orElseThrow(CredenciaisInvalidasException::new);

        String token = jwtService.gerarToken(funcionario);

        return new LoginOutput(token, "Bearer", 3600);
    }
}