package br.com.autorepairshop.controller.ordemservico;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.autorepairshop.application.ordemservico.abrirordemservico.AbrirOrdemServicoUseCase;
import br.com.autorepairshop.application.ordemservico.aprovar.AprovarOrdemServicoUseCase;
import br.com.autorepairshop.application.ordemservico.efetuarentrega.EfetuarEntregaInput;
import br.com.autorepairshop.application.ordemservico.efetuarentrega.EfetuarEntregaUseCase;
import br.com.autorepairshop.application.ordemservico.finalizarservico.FinalizarServicoOrdemServicoInput;
import br.com.autorepairshop.application.ordemservico.finalizarservico.FinalizarServicoUseCase;
import br.com.autorepairshop.application.ordemservico.iniciardiagnostico.IniciarDiagnosticoOrdemServicoInput;
import br.com.autorepairshop.application.ordemservico.iniciardiagnostico.IniciarDiagnosticoUseCase;
import br.com.autorepairshop.application.ordemservico.iniciarservico.IniciarServicoOrdemServicoInput;
import br.com.autorepairshop.application.ordemservico.iniciarservico.IniciarServicoUseCase;
import br.com.autorepairshop.application.ordemservico.pagar.OrdemServicoPagaInput;
import br.com.autorepairshop.application.ordemservico.pagar.OrdemServicoPagaUseCase;
import br.com.autorepairshop.application.ordemservico.reprovar.ReprovarOrdemServicoUseCase;
import br.com.autorepairshop.controller.ordemservico.dto.AbrirOrdemServicoRequest;
import br.com.autorepairshop.controller.ordemservico.dto.AprovarOrdemServicoRequest;
import br.com.autorepairshop.controller.ordemservico.dto.ReprovarOrdemServicoRequest;
import br.com.autorepairshop.infrastructure.security.OpenApiSecurity;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Ordem de Serviço", description = "Gestão de ordens de serviço")
@RestController
@RequestMapping("/api/ordem-servico")
@SecurityRequirement(name = OpenApiSecurity.BEARER_AUTH)
public class OrdemServicoController {

    private final AbrirOrdemServicoUseCase abrirOrdemServicoUseCase;
    
    private final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    
    private final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;
    
    private final ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase;
    
    private final IniciarServicoUseCase iniciarServicoUseCase;
    
    private final FinalizarServicoUseCase finalizarServicoUseCase;
    
    private final OrdemServicoPagaUseCase ordemServicoPagaUseCase;
    
    private final EfetuarEntregaUseCase efetuarEntregaUseCase;
   
    public OrdemServicoController(
    		AbrirOrdemServicoUseCase abrirOrdemServicoUseCase,
    		IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase,
    		AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase,
    		ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase,
    		IniciarServicoUseCase iniciarServicoUseCase,
    		FinalizarServicoUseCase finalizarServicoUseCase,
    		OrdemServicoPagaUseCase ordemServicoPagaUseCase,
    		EfetuarEntregaUseCase efetuarEntregaUseCase) {
        this.abrirOrdemServicoUseCase = abrirOrdemServicoUseCase;
        this.iniciarDiagnosticoUseCase = iniciarDiagnosticoUseCase;
        this.aprovarOrdemServicoUseCase = aprovarOrdemServicoUseCase;
        this.reprovarOrdemServicoUseCase = reprovarOrdemServicoUseCase;
        this.iniciarServicoUseCase = iniciarServicoUseCase;
        this.finalizarServicoUseCase = finalizarServicoUseCase;
        this.ordemServicoPagaUseCase = ordemServicoPagaUseCase;
        this.efetuarEntregaUseCase = efetuarEntregaUseCase;
    }

    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
    @PostMapping
    public ResponseEntity<String> criar(
            @Valid @RequestBody AbrirOrdemServicoRequest request){
        var output = abrirOrdemServicoUseCase.executar(request.toInput());
        return ResponseEntity.ok(output);
    }
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).MECANICO)")
    @PatchMapping("/{id}/iniciar-diagnostico")
    public ResponseEntity<Void> iniciarDiagnostico(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

    	String mecanicoId = jwt.getSubject();
        iniciarDiagnosticoUseCase.executar(new IniciarDiagnosticoOrdemServicoInput(id,mecanicoId));
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{ordemServicoId}/aprovar")
    public ResponseEntity<Void> aprovar(
            @PathVariable String ordemServicoId,
            @Valid @RequestBody AprovarOrdemServicoRequest request) {

        aprovarOrdemServicoUseCase.executar(
                request.toInput(ordemServicoId)
        );

        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{ordemServicoId}/reprovar")
    public ResponseEntity<Void> reprovar(
            @PathVariable String ordemServicoId,
            @Valid @RequestBody ReprovarOrdemServicoRequest request) {

        reprovarOrdemServicoUseCase.executar(
                request.toInput(ordemServicoId)
        );

        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).MECANICO)")
    @PostMapping("/{id}/iniciar-servico")
    public ResponseEntity<Void> iniciarServico(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

    	String mecanicoId = jwt.getSubject();
        iniciarServicoUseCase.executar(new IniciarServicoOrdemServicoInput(id,mecanicoId));
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).MECANICO)")
    @PostMapping("/{id}/finalizar-servico")
    public ResponseEntity<Void> finalizarServico(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

    	String mecanicoId = jwt.getSubject();
        finalizarServicoUseCase.executar(new FinalizarServicoOrdemServicoInput(id,mecanicoId));
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
    @PostMapping("/{id}/registrar-pagamento")
    public ResponseEntity<Void> registrarPagamento(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

    	String atendenteId = jwt.getSubject();
        ordemServicoPagaUseCase.executar(new OrdemServicoPagaInput(id,atendenteId));
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
    @PostMapping("/{id}/entregar-veiculo")
    public ResponseEntity<Void> entregarVeiculo(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

    	String atendenteId = jwt.getSubject();
        efetuarEntregaUseCase.executar(new EfetuarEntregaInput(id,atendenteId));
        return ResponseEntity.noContent().build();
    }
    
   
}