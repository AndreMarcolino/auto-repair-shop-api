package br.com.autorepairshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.autorepairshop.application.veiculo.create.CriarVeiculoUseCase;
import br.com.autorepairshop.application.veiculo.update.AlterarVeiculoUseCase;
import br.com.autorepairshop.controller.veiculo.dto.AlterarVeiculoRequest;
import br.com.autorepairshop.controller.veiculo.dto.CriarVeiculoRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/veiculo")
public class VeiculoController {

    private final CriarVeiculoUseCase criarVeiculoUseCase;
    
    private final AlterarVeiculoUseCase alterarVeiculoUseCase;

    public VeiculoController(CriarVeiculoUseCase criarVeiculoUseCase,
    		AlterarVeiculoUseCase alterarVeiculoUseCase) {
        this.criarVeiculoUseCase = criarVeiculoUseCase;
        this.alterarVeiculoUseCase = alterarVeiculoUseCase;
    }

    @PostMapping
    public ResponseEntity<?> criar(
            @Valid @RequestBody CriarVeiculoRequest request) {

        var veiculoOutput = criarVeiculoUseCase.executar(request.paraInput());

        return ResponseEntity.ok(veiculoOutput.id().toString());
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable String id,
            @Valid @RequestBody AlterarVeiculoRequest request) {

        var input = request.paraInput(id);
        alterarVeiculoUseCase.executar(input);

        return ResponseEntity.noContent().build();
    }
}
