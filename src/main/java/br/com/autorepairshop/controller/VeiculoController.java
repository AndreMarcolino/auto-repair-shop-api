package br.com.autorepairshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.autorepairshop.controller.veiculo.dto.CriarVeiculoRequest;
import br.com.autorepairshop.domain.veiculo.create.CriarVeiculoUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/veiculo")
public class VeiculoController {

    private final CriarVeiculoUseCase criarVeiculoUseCase;

    public VeiculoController(CriarVeiculoUseCase criarVeiculoUseCase) {
        this.criarVeiculoUseCase = criarVeiculoUseCase;
    }

    @PostMapping
    public ResponseEntity<?> criar(
            @Valid @RequestBody CriarVeiculoRequest request) {

        var veiculoOutput = criarVeiculoUseCase.executar(request.paraInput());

        return ResponseEntity.ok(veiculoOutput.id().toString());
    }
}
