package br.com.autorepairshop.controller.servico;

import org.springdoc.core.annotations.ParameterObject;
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
import org.springframework.web.bind.annotation.RestController;

import br.com.autorepairshop.application.servico.alterar.AlterarServicoUseCase;
import br.com.autorepairshop.application.servico.buscarservicoporid.BuscarServicoPorIdUseCase;
import br.com.autorepairshop.application.servico.criar.CriarServicoUseCase;
import br.com.autorepairshop.application.servico.excluir.ExcluirServicoUseCase;
import br.com.autorepairshop.application.servico.listar.ListarServicosUseCase;
import br.com.autorepairshop.application.shared.dto.PaginacaoInput;
import br.com.autorepairshop.controller.servico.dto.AlterarServicoRequest;
import br.com.autorepairshop.controller.servico.dto.CriarServicoRequest;
import br.com.autorepairshop.controller.servico.dto.ListarServicosRequest;
import br.com.autorepairshop.controller.servico.dto.ListarServicosResponse;
import br.com.autorepairshop.controller.servico.dto.ServicoResponse;
import br.com.autorepairshop.infrastructure.security.OpenApiSecurity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Servico", description = "Gestão administrativa CRUD de serviços")
@RestController
@RequestMapping("/api/servicos")
@SecurityRequirement(name = OpenApiSecurity.BEARER_AUTH)

public class ServicoController {

    private final CriarServicoUseCase criarServico;
    
    private final AlterarServicoUseCase alterarServico;
    
    private final ExcluirServicoUseCase excluirServico;
    
    private final ListarServicosUseCase listarServicos;
    
    private final BuscarServicoPorIdUseCase buscarServicoPorId;
    
   
    public ServicoController(CriarServicoUseCase criarServico,
    		AlterarServicoUseCase alterarServico,
    		ExcluirServicoUseCase excluirServico,
    		ListarServicosUseCase listarServicos,
    		BuscarServicoPorIdUseCase buscarServicoPorId)
    {
        this.criarServico = criarServico;
        this.alterarServico = alterarServico;
        this.excluirServico = excluirServico;
        this.listarServicos = listarServicos;
        this.buscarServicoPorId = buscarServicoPorId;
       
        }

    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
    @PostMapping
    public ResponseEntity<?> criar(
            @Valid @RequestBody CriarServicoRequest request) {

        var servicoInput = criarServico.executar(request.toInput());
        return ResponseEntity.ok(servicoInput.id().toString());
    }
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
    @PatchMapping("/{id}")
    public ResponseEntity<?> alterar(
            @Parameter(description = "ID", example = "33333333-3333-3333-3333-000000000001") @PathVariable String id,
            @Valid @RequestBody AlterarServicoRequest request) {

        var input = request.toInput(id);
        alterarServico.executar(input);

        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasAnyRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE, T(br.com.autorepairshop.infrastructure.security.Roles).MECANICO)")
    @GetMapping
    public ResponseEntity<ListarServicosResponse> listar(
            @ParameterObject @ModelAttribute ListarServicosRequest request
            
    ) {
        var inputRequest = request.toInput();
        var inputPaginacao = new PaginacaoInput(request.pagina(),request.tamanho());
        var listaServicosOutput = listarServicos.executar(inputRequest,inputPaginacao);
        var response = ListarServicosResponse.toResponse(listaServicosOutput);

        return ResponseEntity.ok(response);
    }
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@Parameter(description = "ID", example = "33333333-3333-3333-3333-000000000006") @PathVariable String id) {
        excluirServico.executar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE, T(br.com.autorepairshop.infrastructure.security.Roles).MECANICO)")
  	public ResponseEntity<ServicoResponse> buscarServicoPorId(@Parameter(description = "ID", example = "33333333-3333-3333-3333-000000000001") @PathVariable String id) {
  		var output = this.buscarServicoPorId.executar(id);
  		if(output.isPresent())
  		{
  			return ResponseEntity.ok(ServicoResponse.toResponse(output.get())); 
  		}
  		return ResponseEntity.notFound().build();
  	}
    
}
