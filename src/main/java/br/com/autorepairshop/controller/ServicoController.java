package br.com.autorepairshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.autorepairshop.application.servico.criar.CriarServicoUseCase;
import br.com.autorepairshop.controller.servico.dto.CriarServicoRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/servico")
public class ServicoController {

    private final CriarServicoUseCase criarServicoUseCase;
    
   
    public ServicoController(CriarServicoUseCase criarServicoUseCase)
    {
        this.criarServicoUseCase = criarServicoUseCase;
       
        }

    @PostMapping
    public ResponseEntity<?> criar(
            @Valid @RequestBody CriarServicoRequest request) {

        var servicoInput = criarServicoUseCase.executar(request.paraInput());
        return ResponseEntity.ok(servicoInput.id().toString());
    }
    
}
