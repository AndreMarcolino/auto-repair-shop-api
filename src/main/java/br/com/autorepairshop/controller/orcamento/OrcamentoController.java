package br.com.autorepairshop.controller.orcamento;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.autorepairshop.application.orcamento.adicionarinsumo.AdicionarInsumoUseCase;
import br.com.autorepairshop.application.orcamento.adicionarservico.AdicionarServicoUseCase;
import br.com.autorepairshop.application.orcamento.finalizar.FinalizarOrcamentoUseCase;
import br.com.autorepairshop.controller.orcamento.dto.AdicionarInsumoRequest;
import br.com.autorepairshop.controller.orcamento.dto.AdicionarServicoRequest;
import br.com.autorepairshop.controller.orcamento.dto.FinalizarOrcamentoRequest;
import br.com.autorepairshop.infrastructure.security.OpenApiSecurity;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Orçamento", description = "Gestão do Orçamento")
@RestController
@RequestMapping("/api/ordens-servico/{id}/orcamento")
@SecurityRequirement(name = OpenApiSecurity.BEARER_AUTH)
public class OrcamentoController {

    private final AdicionarInsumoUseCase adicionarInsumo;
    
    private final AdicionarServicoUseCase adicionarServico;
    
    private final FinalizarOrcamentoUseCase finalizarOrcamento;
	
    
    public OrcamentoController(AdicionarInsumoUseCase adicionarInsumo,
    		AdicionarServicoUseCase adicionarServico,
    		FinalizarOrcamentoUseCase finalizarOrcamento) {
    	this.adicionarInsumo = adicionarInsumo;
    	this.adicionarServico = adicionarServico;
    	this.finalizarOrcamento = finalizarOrcamento;
    }

    @PostMapping("/adicionar-insumos")
    public ResponseEntity<?> adicionarInsumos(
    		@PathVariable String id,
            @RequestBody AdicionarInsumoRequest request,
            @AuthenticationPrincipal Jwt jwt) {

    	String mecanicoId = jwt.getSubject();
    	var input = request.paraInput(id,mecanicoId);
        adicionarInsumo.executar(input);         
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/adicionar-servicos")
    public ResponseEntity<?> adicionarServicos(
            @PathVariable String id,
            @RequestBody AdicionarServicoRequest request,
            @AuthenticationPrincipal Jwt jwt) {

    	String mecanicoId = jwt.getSubject();
    	var input = request.paraInput(id,mecanicoId);
    	adicionarServico.executar(input);         
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/finalizar")
    public ResponseEntity<Void> finalizarOrcamento(
            @PathVariable String id,
            @RequestBody FinalizarOrcamentoRequest request) {

        var input = request.paraInput(id);
        finalizarOrcamento.executar(input);

        return ResponseEntity.noContent().build();
    }
}
