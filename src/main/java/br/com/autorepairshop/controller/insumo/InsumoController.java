package br.com.autorepairshop.controller.insumo;

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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Insumo", description = "Gestão administrativa CRUD de peças e insumos")
@RestController
@RequestMapping("/api/insumos")
@SecurityRequirement(name = "bearerAuth")
public class InsumoController {

    private final CriarInsumoUseCase criarInsumo;
    
    private final AlterarInsumoUseCase alterarInsumo;
    
    private final ListarInsumosUseCase listarInsumos;
    
    private final ExcluirInsumoUseCase excluirInsumo;
    
    private final BuscarInsumoPorIdUseCase buscarInsumoPorId;
    
    
    public InsumoController(CriarInsumoUseCase criarInsumo,
    		AlterarInsumoUseCase alterarInsumo,
    		ListarInsumosUseCase listarInsumos,
    		ExcluirInsumoUseCase excluirInsumo,
    		BuscarInsumoPorIdUseCase buscarInsumoPorId)
    {
        this.criarInsumo = criarInsumo;
        this.alterarInsumo = alterarInsumo;
        this.listarInsumos = listarInsumos;
        this.excluirInsumo = excluirInsumo;
        this.buscarInsumoPorId = buscarInsumoPorId;
        }

    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
    @PostMapping
    public ResponseEntity<?> criar(
            @Valid @RequestBody CriarInsumoRequest request) {

        var servicoOutput = criarInsumo.executar(request.toInput());

        return ResponseEntity.ok(servicoOutput.id().toString());
    }
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
    @PatchMapping("/{id}")
    public ResponseEntity<?> alterar(
            @PathVariable String id,
            @Valid @RequestBody AlterarInsumoRequest request) {

        var input = request.toInput(id);
        alterarInsumo.executar(input);
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasAnyRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE, T(br.com.autorepairshop.infrastructure.security.Roles).MECANICO)")
    @GetMapping
    public ResponseEntity<ListarInsumosResponse> listar(
            @ModelAttribute ListarInsumosRequest request
            
    ) {
        var inputRequest = request.toInput();
        var inputPaginacao = new PaginacaoInput(request.pagina(),request.tamanho());
        var listaServicosOutput = listarInsumos.executar(inputRequest,inputPaginacao);
        var response = ListarInsumosResponse.toResponse(listaServicosOutput);

        return ResponseEntity.ok(response);
    }
    
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        excluirInsumo.executar(id);
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasAnyRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE, T(br.com.autorepairshop.infrastructure.security.Roles).MECANICO)")
    @GetMapping("/{id}")
	public ResponseEntity<InsumoResponse> buscarInsumoPorId(@RequestParam String id) {
		var output = this.buscarInsumoPorId.executar(id);
		if(output.isPresent())
		{
			return ResponseEntity.ok(InsumoResponse.toResponse(output.get())); 
		}
		return ResponseEntity.notFound().build();
	}
    
}
