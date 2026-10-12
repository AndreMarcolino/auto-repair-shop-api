package br.com.autorepairshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.autorepairshop.application.peca.criar.CriarPecaUseCase;
import br.com.autorepairshop.controller.peca.dto.CriarPecaRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Peça", description = "Gestão administrativa CRUD de peças e insumos")
@RestController
@RequestMapping("/api/peca")
public class PecaController {

    private final CriarPecaUseCase criarPecaUseCase;
    
    
    public PecaController(CriarPecaUseCase criarPecaUseCase)
    {
        this.criarPecaUseCase = criarPecaUseCase;
        
        }

    @PostMapping
    public ResponseEntity<?> criar(
            @Valid @RequestBody CriarPecaRequest request) {

        var servicoOutput = criarPecaUseCase.executar(request.paraInput());

        return ResponseEntity.ok(servicoOutput.id().toString());
    }
    
}
