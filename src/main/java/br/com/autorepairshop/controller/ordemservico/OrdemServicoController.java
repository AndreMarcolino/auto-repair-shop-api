package br.com.autorepairshop.controller.ordemservico;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.autorepairshop.application.ordemservico.abrirordemservico.AbrirOrdemServicoUseCase;
import br.com.autorepairshop.application.ordemservico.enviarorcamento.EnviarOrcamentoInput;
import br.com.autorepairshop.application.ordemservico.enviarorcamento.EnviarOrcamentoUseCase;
import br.com.autorepairshop.application.ordemservico.gerarorcamento.GerarOrcamentoInput;
import br.com.autorepairshop.application.ordemservico.gerarorcamento.GerarOrcamentoUseCase;
import br.com.autorepairshop.application.ordemservico.iniciardiagnostico.IniciarDiagnosticoOrdemServicoInput;
import br.com.autorepairshop.application.ordemservico.iniciardiagnostico.IniciarDiagnosticoUseCase;
import br.com.autorepairshop.controller.ordemservico.dto.AbrirOrdemServicoRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Ordem de Serviço", description = "Gestão de ordens de serviço")
@RestController
@RequestMapping("/api/ordem-servico")
public class OrdemServicoController {

    private final AbrirOrdemServicoUseCase abrirOrdemServicoUseCase;
    
    private final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    
    private final GerarOrcamentoUseCase gerarOrcamentoUseCase;
    
    private final EnviarOrcamentoUseCase enviarOrcamentoUseCase;
   
    public OrdemServicoController(
    		AbrirOrdemServicoUseCase abrirOrdemServicoUseCase,
    		IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase,
    		GerarOrcamentoUseCase gerarOrcamentoUseCase,
    		EnviarOrcamentoUseCase enviarOrcamentoUseCase) {
        this.abrirOrdemServicoUseCase = abrirOrdemServicoUseCase;
        this.iniciarDiagnosticoUseCase = iniciarDiagnosticoUseCase;
        this.gerarOrcamentoUseCase = gerarOrcamentoUseCase;
        this.enviarOrcamentoUseCase = enviarOrcamentoUseCase;
    }

    @PostMapping
    public ResponseEntity<String> criar(
            @Valid @RequestBody AbrirOrdemServicoRequest request) {

        var output = abrirOrdemServicoUseCase.executar(request.toInput());
        return ResponseEntity.ok(output);
    }
    
    @PatchMapping("/{id}/iniciar-diagnostico")
    public ResponseEntity<Void> iniciarDiagnostico(
            @PathVariable String id) {
        iniciarDiagnosticoUseCase.executar(new IniciarDiagnosticoOrdemServicoInput(id));
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/gerar-orcamento")
    public ResponseEntity<Void> gerarOrcamento(
            @PathVariable String id) {
        gerarOrcamentoUseCase.executar(new GerarOrcamentoInput(id));
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/enviar-orcamento")
    public ResponseEntity<Void> enviarOrcamento(
            @PathVariable String id) {
        enviarOrcamentoUseCase.executar(new EnviarOrcamentoInput(id));
        return ResponseEntity.noContent().build();
    }
}