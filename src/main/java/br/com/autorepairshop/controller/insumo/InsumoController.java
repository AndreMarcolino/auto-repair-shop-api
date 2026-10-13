package br.com.autorepairshop.controller.insumo;

import org.springframework.http.ResponseEntity;
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

import br.com.autorepairshop.application.insumo.alterar.AlterarInsumoUseCase;
import br.com.autorepairshop.application.insumo.buscarinsumoporid.BuscarInsumoPorIdUseCase;
import br.com.autorepairshop.application.insumo.criar.CriarInsumoUseCase;
import br.com.autorepairshop.application.insumo.excluir.ExcluirInsumoUseCase;
import br.com.autorepairshop.application.insumo.listar.ListarInsumosUseCase;
import br.com.autorepairshop.application.shared.dto.PaginacaoInput;
import br.com.autorepairshop.controller.insumo.dto.AlterarInsumoRequest;
import br.com.autorepairshop.controller.insumo.dto.CriarInsumoRequest;
import br.com.autorepairshop.controller.insumo.dto.InsumoResponse;
import br.com.autorepairshop.controller.insumo.dto.ListarInsumosRequest;
import br.com.autorepairshop.controller.insumo.dto.ListarInsumosResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Insumo", description = "Gestão administrativa CRUD de peças e insumos")
@RestController
@RequestMapping("/api/insumo")
public class InsumoController {

    private final CriarInsumoUseCase criarInsumoUseCase;
    
    private final AlterarInsumoUseCase alterarInsumoUseCase;
    
    private final ListarInsumosUseCase listarInsumosUseCase;
    
    private final ExcluirInsumoUseCase excluirInsumoUseCase;
    
    private final BuscarInsumoPorIdUseCase buscarInsumoPorIdUseCase;
    
    
    public InsumoController(CriarInsumoUseCase criarInsumoUseCase,
    		AlterarInsumoUseCase alterarInsumoUseCase,
    		ListarInsumosUseCase listarInsumosUseCase,
    		ExcluirInsumoUseCase excluirInsumoUseCase,
    		BuscarInsumoPorIdUseCase buscarInsumoPorIdUseCase)
    {
        this.criarInsumoUseCase = criarInsumoUseCase;
        this.alterarInsumoUseCase = alterarInsumoUseCase;
        this.listarInsumosUseCase = listarInsumosUseCase;
        this.excluirInsumoUseCase = excluirInsumoUseCase;
        this.buscarInsumoPorIdUseCase = buscarInsumoPorIdUseCase;
        }

    @PostMapping
    public ResponseEntity<?> criar(
            @Valid @RequestBody CriarInsumoRequest request) {

        var servicoOutput = criarInsumoUseCase.executar(request.toInput());

        return ResponseEntity.ok(servicoOutput.id().toString());
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> alterar(
            @PathVariable String id,
            @Valid @RequestBody AlterarInsumoRequest request) {

        var input = request.toInput(id);
        alterarInsumoUseCase.executar(input);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<ListarInsumosResponse> listar(
            @ModelAttribute ListarInsumosRequest request
            
    ) {
        var inputRequest = request.toInput();
        var inputPaginacao = new PaginacaoInput(request.pagina(),request.tamanho());
        var listaServicosOutput = listarInsumosUseCase.executar(inputRequest,inputPaginacao);
        var response = ListarInsumosResponse.toResponse(listaServicosOutput);

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        excluirInsumoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
	public ResponseEntity<InsumoResponse> buscarInsumoPorId(@RequestParam String id) {
		var output = this.buscarInsumoPorIdUseCase.executar(id);
		if(output.isPresent())
		{
			return ResponseEntity.ok(InsumoResponse.toResponse(output.get())); 
		}
		return ResponseEntity.notFound().build();
	}
    
}
