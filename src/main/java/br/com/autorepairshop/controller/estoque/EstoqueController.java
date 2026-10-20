package br.com.autorepairshop.controller.estoque;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import br.com.autorepairshop.controller.estoque.dto.RegistrarEntradaEstoqueRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import br.com.autorepairshop.application.estoque.registrarentrada.RegistrarEntradaEstoqueUseCase;

@RestController
@RequestMapping("/api/estoques")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
public class EstoqueController {

    private final RegistrarEntradaEstoqueUseCase registrarEntradaEstoqueUseCase;

    public EstoqueController(
            RegistrarEntradaEstoqueUseCase registrarEntradaEstoqueUseCase) {
        this.registrarEntradaEstoqueUseCase = registrarEntradaEstoqueUseCase;
    }

    @PostMapping("/entradas")
    public ResponseEntity<Void> registrarEntrada(
            @Valid @RequestBody RegistrarEntradaEstoqueRequest request) {

        registrarEntradaEstoqueUseCase.executar(request.paraInput());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}