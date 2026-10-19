package br.com.autorepairshop.controller.orcamento;

import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/ordens-servico/{ordemServicoId}/orcamento")
@SecurityRequirement(name = OpenApiSecurity.BEARER_AUTH)
public class OrcamentoController {

    private final AdicionarInsumoUseCase adicionarInsumoUseCase;
    
    private final AdicionarServicoUseCase adicionarServicoUseCase;
    
    private final FinalizarOrcamentoUseCase finalizarOrcamentoUseCase;
	
    
    public OrcamentoController(AdicionarInsumoUseCase adicionarInsumoUseCase,
    		AdicionarServicoUseCase adicionarServicoUseCase,
    		FinalizarOrcamentoUseCase finalizarOrcamentoUseCase) {
    	this.adicionarInsumoUseCase = adicionarInsumoUseCase;
    	this.adicionarServicoUseCase = adicionarServicoUseCase;
    	this.finalizarOrcamentoUseCase = finalizarOrcamentoUseCase;
    }

    @PostMapping("/adicionar-insumos")
    public ResponseEntity<?> adicionarInsumos(
            @PathVariable String ordemServicoId,
            @RequestBody AdicionarInsumoRequest request) {

    	var input = request.paraInput(ordemServicoId,"5cd064ed-7904-43ab-9cd9-b72684023de9");
        adicionarInsumoUseCase.executar(input);         
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/adicionar-servicos")
    public ResponseEntity<?> adicionarServicos(
            @PathVariable String ordemServicoId,
            @RequestBody AdicionarServicoRequest request) {

    	var input = request.paraInput(ordemServicoId,"5cd064ed-7904-43ab-9cd9-b72684023de9");
    	adicionarServicoUseCase.executar(input);         
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/finalizar")
    public ResponseEntity<Void> finalizarOrcamento(
            @PathVariable String ordemServicoId,
            @RequestBody FinalizarOrcamentoRequest request) {

        var input = request.paraInput(ordemServicoId);
        finalizarOrcamentoUseCase.executar(input);

        return ResponseEntity.noContent().build();
    }
}
