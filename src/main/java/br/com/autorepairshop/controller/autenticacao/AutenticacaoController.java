package br.com.autorepairshop.controller.autenticacao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.autorepairshop.application.autenticacao.AutenticarFuncionarioUseCase;
import br.com.autorepairshop.application.autenticacao.LoginOutput;
import br.com.autorepairshop.controller.autenticacao.dto.LoginRequest;
import br.com.autorepairshop.controller.autenticacao.dto.LoginResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Autenticação do funcionário")
@RestController
@RequestMapping("/api/autenticacao")
public class AutenticacaoController {

    private final AutenticarFuncionarioUseCase autenticarFuncionarioUseCase;

    public AutenticacaoController(
            AutenticarFuncionarioUseCase autenticarFuncionarioUseCase) {
        this.autenticarFuncionarioUseCase = autenticarFuncionarioUseCase;
    }

	@GetMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginOutput output = autenticarFuncionarioUseCase.executar(request.paraInput());

        LoginResponse response = new LoginResponse(
                output.accessToken(),
                output.tokenType(),
                output.expiresIn()
        );

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/gera-token-mecanico")
    public ResponseEntity<LoginResponse> geraTokenMecanico() {

    	LoginRequest request = new LoginRequest("mecanico@auto-repair-shop.com.br", "123456");
        LoginOutput output = autenticarFuncionarioUseCase.executar(request.paraInput());

        LoginResponse response = new LoginResponse(
                output.accessToken(),
                output.tokenType(),
                output.expiresIn()
        );

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/gera-token-atendente")
    public ResponseEntity<LoginResponse> geraTokenAtendente() {

    	LoginRequest request = new LoginRequest("atendente@auto-repair-shop.com.br", "123456");
        LoginOutput output = autenticarFuncionarioUseCase.executar(request.paraInput());

        LoginResponse response = new LoginResponse(
                output.accessToken(),
                output.tokenType(),
                output.expiresIn()
        );

        return ResponseEntity.ok(response);
    }
}

