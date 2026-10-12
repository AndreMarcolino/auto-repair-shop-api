package br.com.autorepairshop.controller.servico;

import org.springframework.http.ResponseEntity;
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
import br.com.autorepairshop.application.servico.criar.CriarServicoUseCase;
import br.com.autorepairshop.application.servico.excluir.ExcluirServicoUseCase;
import br.com.autorepairshop.application.servico.listar.ListarServicosUseCase;
import br.com.autorepairshop.application.shared.dto.PaginacaoInput;
import br.com.autorepairshop.controller.servico.dto.AlterarServicoRequest;
import br.com.autorepairshop.controller.servico.dto.CriarServicoRequest;
import br.com.autorepairshop.controller.servico.dto.ListarServicosRequest;
import br.com.autorepairshop.controller.servico.dto.ListarServicosResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Servico", description = "Gestão administrativa CRUD de serviços")
@RestController
@RequestMapping("/api/servico")
public class ServicoController {

    private final CriarServicoUseCase criarServicoUseCase;
    
    private final AlterarServicoUseCase alterarServicoUseCase;
    
    private final ExcluirServicoUseCase excluirServicoUseCase;
    
    private final ListarServicosUseCase listarServicosUseCase;
    
   
    public ServicoController(CriarServicoUseCase criarServicoUseCase,
    		AlterarServicoUseCase alterarServicoUseCase,
    		ExcluirServicoUseCase excluirServicoUseCase,
    		ListarServicosUseCase listarServicosUseCase)
    {
        this.criarServicoUseCase = criarServicoUseCase;
        this.alterarServicoUseCase = alterarServicoUseCase;
        this.excluirServicoUseCase = excluirServicoUseCase;
        this.listarServicosUseCase = listarServicosUseCase;
       
        }

    @PostMapping
    public ResponseEntity<?> criar(
            @Valid @RequestBody CriarServicoRequest request) {

        var servicoInput = criarServicoUseCase.executar(request.toInput());
        return ResponseEntity.ok(servicoInput.id().toString());
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> alterar(
            @PathVariable String id,
            @Valid @RequestBody AlterarServicoRequest request) {

        var input = request.toInput(id);
        alterarServicoUseCase.executar(input);

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<ListarServicosResponse> listar(
            @ModelAttribute ListarServicosRequest request
            
    ) {
        var inputRequest = request.toInput();
        var inputPaginacao = new PaginacaoInput(request.pagina(),request.tamanho());
        var listaServicosOutput = listarServicosUseCase.executar(inputRequest,inputPaginacao);
        var response = ListarServicosResponse.toResponse(listaServicosOutput);

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        excluirServicoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
    
}
