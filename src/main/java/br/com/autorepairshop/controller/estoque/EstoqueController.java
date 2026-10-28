package br.com.autorepairshop.controller.estoque;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import br.com.autorepairshop.controller.estoque.dto.RegistrarEntradaEstoqueRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import br.com.autorepairshop.application.estoque.registrarentrada.RegistrarEntradaEstoqueUseCase;

@Tag(name = "Estoque", description = "Gestão de estoque da oficina")
@RestController
@RequestMapping("/api/estoque")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
public class EstoqueController {

    private final RegistrarEntradaEstoqueUseCase registrarEntradaEstoque;

    public EstoqueController(
            RegistrarEntradaEstoqueUseCase registrarEntradaEstoque) {
        this.registrarEntradaEstoque = registrarEntradaEstoque;
    }

    @PostMapping("/entrada")
    public ResponseEntity<Void> registrarEntrada(
            @Valid @RequestBody RegistrarEntradaEstoqueRequest request) {

        registrarEntradaEstoque.executar(request.paraInput());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}