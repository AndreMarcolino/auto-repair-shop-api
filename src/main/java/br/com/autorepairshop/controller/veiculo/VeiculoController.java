package br.com.autorepairshop.controller.veiculo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.autorepairshop.application.shared.dto.PaginacaoInput;
import br.com.autorepairshop.application.veiculo.alterar.AlterarVeiculoUseCase;
import br.com.autorepairshop.application.veiculo.buscarveiculoporid.BuscarVeiculoPorIdUseCase;
import br.com.autorepairshop.application.veiculo.criar.CriarVeiculoUseCase;
import br.com.autorepairshop.application.veiculo.excluir.ExcluirVeiculoUseCase;
import br.com.autorepairshop.application.veiculo.listar.ListarVeiculosUseCase;
import br.com.autorepairshop.controller.veiculo.dto.AlterarVeiculoRequest;
import br.com.autorepairshop.controller.veiculo.dto.CriarVeiculoRequest;
import br.com.autorepairshop.controller.veiculo.dto.ListarVeiculosRequest;
import br.com.autorepairshop.controller.veiculo.dto.ListarVeiculosResponse;
import br.com.autorepairshop.controller.veiculo.dto.VeiculoResponse;
import br.com.autorepairshop.infrastructure.security.OpenApiSecurity;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Veiculo", description = "Gestão administrativa CRUD DE VEICULOS")
@RestController
@RequestMapping("/api/veiculo")
@SecurityRequirement(name = OpenApiSecurity.BEARER_AUTH)
@PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")

public class VeiculoController {

    private final CriarVeiculoUseCase criarVeiculoUseCase;
    
    private final AlterarVeiculoUseCase alterarVeiculoUseCase;
    
    private final ListarVeiculosUseCase listarVeiculosUseCase;
    
    private final ExcluirVeiculoUseCase excluirVeiculoUseCase;
    
    private final BuscarVeiculoPorIdUseCase buscarVeiculoPorIdUseCase;
    
    public VeiculoController(CriarVeiculoUseCase criarVeiculoUseCase,
    		AlterarVeiculoUseCase alterarVeiculoUseCase,
    		ListarVeiculosUseCase listarVeiculosUseCase,
    		ExcluirVeiculoUseCase excluirVeiculoUseCase,
    		BuscarVeiculoPorIdUseCase buscarVeiculoPorIdUseCase)
    {
        this.criarVeiculoUseCase = criarVeiculoUseCase;
        this.alterarVeiculoUseCase = alterarVeiculoUseCase;
        this.listarVeiculosUseCase = listarVeiculosUseCase;
        this.excluirVeiculoUseCase = excluirVeiculoUseCase;
        this.buscarVeiculoPorIdUseCase = buscarVeiculoPorIdUseCase;
        
        }

    @PostMapping
    public ResponseEntity<?> criar(
            @Valid @RequestBody CriarVeiculoRequest request) {

        var veiculoOutput = criarVeiculoUseCase.executar(request.toInput());

        return ResponseEntity.ok(veiculoOutput.id().toString());
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> alterar(
            @PathVariable String id,
            @Valid @RequestBody AlterarVeiculoRequest request) {

        var input = request.toInput(id);
        alterarVeiculoUseCase.executar(input);

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<ListarVeiculosResponse> listar(
            @ModelAttribute ListarVeiculosRequest request
    ) {
        var inputRequest = request.toInput();
        var inputPaginacao = new PaginacaoInput(request.pagina(),request.tamanho());
        var listaVeiculosOutput = listarVeiculosUseCase.executar(inputRequest, inputPaginacao);
        var response = ListarVeiculosResponse.toResponse(listaVeiculosOutput);

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        excluirVeiculoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
  	public ResponseEntity<VeiculoResponse> buscarVeiculoPorId(@RequestParam String id) {
  		var output = this.buscarVeiculoPorIdUseCase.executar(id);
  		if(output.isPresent())
  		{
  			return ResponseEntity.ok(VeiculoResponse.toResponse(output.get())); 
  		}
  		return ResponseEntity.notFound().build();
  	}
}
