package br.com.autorepairshop.controller.ordemservico;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.autorepairshop.application.ordemservico.abrirordemservico.AbrirOrdemServicoUseCase;
import br.com.autorepairshop.application.ordemservico.aprovar.AprovarOrdemServicoUseCase;
import br.com.autorepairshop.application.ordemservico.calculartempomedioexecucao.CalcularTempoMedioExecucaoUseCase;
import br.com.autorepairshop.application.ordemservico.detalhar.DetalharOrdemServicoOutput;
import br.com.autorepairshop.application.ordemservico.detalhar.DetalharOrdemServicoUseCase;
import br.com.autorepairshop.application.ordemservico.detalharcliente.DetalharOrdemServicoClienteInput;
import br.com.autorepairshop.application.ordemservico.detalharcliente.DetalharOrdemServicoClienteUseCase;
import br.com.autorepairshop.application.ordemservico.efetuarentrega.EfetuarEntregaInput;
import br.com.autorepairshop.application.ordemservico.efetuarentrega.EfetuarEntregaUseCase;
import br.com.autorepairshop.application.ordemservico.finalizarservico.FinalizarServicoOrdemServicoInput;
import br.com.autorepairshop.application.ordemservico.finalizarservico.FinalizarServicoUseCase;
import br.com.autorepairshop.application.ordemservico.iniciardiagnostico.IniciarDiagnosticoOrdemServicoInput;
import br.com.autorepairshop.application.ordemservico.iniciardiagnostico.IniciarDiagnosticoUseCase;
import br.com.autorepairshop.application.ordemservico.iniciarservico.IniciarServicoOrdemServicoInput;
import br.com.autorepairshop.application.ordemservico.iniciarservico.IniciarServicoUseCase;
import br.com.autorepairshop.application.ordemservico.listar.ListarOrdensServicoUseCase;
import br.com.autorepairshop.application.ordemservico.pagar.OrdemServicoPagaInput;
import br.com.autorepairshop.application.ordemservico.pagar.OrdemServicoPagaUseCase;
import br.com.autorepairshop.application.ordemservico.reprovar.ReprovarOrdemServicoUseCase;
import br.com.autorepairshop.controller.ordemservico.dto.AbrirOrdemServicoRequest;
import br.com.autorepairshop.controller.ordemservico.dto.AbrirOrdemServicoResponse;
import br.com.autorepairshop.controller.ordemservico.dto.AprovarOrdemServicoRequest;
import br.com.autorepairshop.controller.ordemservico.dto.CalcularTempoMedioExecucaoResponse;
import br.com.autorepairshop.controller.ordemservico.dto.DetalharOrdemServicoClienteResponse;
import br.com.autorepairshop.controller.ordemservico.dto.DetalharOrdemServicoResponse;
import br.com.autorepairshop.controller.ordemservico.dto.ListarOrdensServicoResponse;
import br.com.autorepairshop.controller.ordemservico.dto.ReprovarOrdemServicoRequest;
import br.com.autorepairshop.infrastructure.security.OpenApiSecurity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Ordem de Serviço", description = "Gestão de ordens de serviço")
@RestController
@RequestMapping("/api/ordens-servico")
@SecurityRequirement(name = OpenApiSecurity.BEARER_AUTH)
public class OrdemServicoController {

    private final AbrirOrdemServicoUseCase abrirOrdemServico;
    
    private final IniciarDiagnosticoUseCase iniciarDiagnostico;
    
    private final AprovarOrdemServicoUseCase aprovarOrdemServico;
    
    private final ReprovarOrdemServicoUseCase reprovarOrdemServico;
    
    private final IniciarServicoUseCase iniciarServico;
    
    private final FinalizarServicoUseCase finalizarServico;
    
    private final OrdemServicoPagaUseCase ordemServicoPaga;
    
    private final EfetuarEntregaUseCase efetuarEntrega;
    
    private final ListarOrdensServicoUseCase listarOrdensServico;
    
    private final DetalharOrdemServicoUseCase detalharOrdemServico;
    
    private final DetalharOrdemServicoClienteUseCase detalharOrdemServicoCliente;
    
    private final CalcularTempoMedioExecucaoUseCase calcularTempoMedioExecucao;
   
    public OrdemServicoController(
    		AbrirOrdemServicoUseCase abrirOrdemServico,
    		IniciarDiagnosticoUseCase iniciarDiagnostico,
    		AprovarOrdemServicoUseCase aprovarOrdemServico,
    		ReprovarOrdemServicoUseCase reprovarOrdemServico,
    		IniciarServicoUseCase iniciarServico,
    		FinalizarServicoUseCase finalizarServico,
    		OrdemServicoPagaUseCase ordemServicoPaga,
    		EfetuarEntregaUseCase efetuarEntrega,
    		ListarOrdensServicoUseCase listarOrdensServico,
    		DetalharOrdemServicoUseCase detalharOrdemServico,
    		DetalharOrdemServicoClienteUseCase detalharOrdemServicoCliente,
    		CalcularTempoMedioExecucaoUseCase calcularTempoMedioExecucao) {
        this.abrirOrdemServico = abrirOrdemServico;
        this.iniciarDiagnostico = iniciarDiagnostico;
        this.aprovarOrdemServico = aprovarOrdemServico;
        this.reprovarOrdemServico = reprovarOrdemServico;
        this.iniciarServico = iniciarServico;
        this.finalizarServico = finalizarServico;
        this.ordemServicoPaga = ordemServicoPaga;
        this.efetuarEntrega = efetuarEntrega;
        this.listarOrdensServico = listarOrdensServico;
        this.detalharOrdemServico = detalharOrdemServico;
        this.detalharOrdemServicoCliente  = detalharOrdemServicoCliente;
        this.calcularTempoMedioExecucao = calcularTempoMedioExecucao;
    }

    
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
    @PostMapping
    public ResponseEntity<AbrirOrdemServicoResponse> criar(
            @Valid @RequestBody AbrirOrdemServicoRequest request){
        var output = abrirOrdemServico.executar(request.toInput());
        var response = AbrirOrdemServicoResponse.toResponse(output);
        return ResponseEntity.ok(response);
    }
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).MECANICO)")
    @PatchMapping("/{id}/iniciar-diagnostico")
    public ResponseEntity<Void> iniciarDiagnostico(
            @Parameter(description = "ID da ordem de serviço", example = "55555555-5555-5555-5555-000000000001") @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

    	String mecanicoId = jwt.getSubject();
        iniciarDiagnostico.executar(new IniciarDiagnosticoOrdemServicoInput(id,mecanicoId));
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/aprovar")
    public ResponseEntity<Void> aprovar(
            @Valid @RequestBody AprovarOrdemServicoRequest request) {

        aprovarOrdemServico.executar(request.toInput());
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/reprovar")
    public ResponseEntity<Void> reprovar(
            @Valid @RequestBody ReprovarOrdemServicoRequest request) {

        reprovarOrdemServico.executar(request.toInput());
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).MECANICO)")
    @PostMapping("/{id}/iniciar-servico")
    public ResponseEntity<Void> iniciarServico(
            @Parameter(description = "ID da ordem de serviço", example = "55555555-5555-5555-5555-000000000006") @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

    	String mecanicoId = jwt.getSubject();
        iniciarServico.executar(new IniciarServicoOrdemServicoInput(id,mecanicoId));
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).MECANICO)")
    @PostMapping("/{id}/finalizar-servico")
    public ResponseEntity<Void> finalizarServico(
            @Parameter(description = "ID da ordem de serviço", example = "55555555-5555-5555-5555-000000000007") @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

    	String mecanicoId = jwt.getSubject();
        finalizarServico.executar(new FinalizarServicoOrdemServicoInput(id,mecanicoId));
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
    @PostMapping("/{id}/registrar-pagamento")
    public ResponseEntity<Void> registrarPagamento(
            @Parameter(description = "ID da ordem de serviço", example = "55555555-5555-5555-5555-000000000008") @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

    	String atendenteId = jwt.getSubject();
        ordemServicoPaga.executar(new OrdemServicoPagaInput(id,atendenteId));
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
    @PostMapping("/{id}/entregar-veiculo")
    public ResponseEntity<Void> entregarVeiculo(
            @Parameter(description = "ID da ordem de serviço", example = "55555555-5555-5555-5555-000000000009") @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

    	String atendenteId = jwt.getSubject();
        efetuarEntrega.executar(new EfetuarEntregaInput(id,atendenteId));
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasAnyRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE, T(br.com.autorepairshop.infrastructure.security.Roles).MECANICO)")
    @GetMapping
    public ResponseEntity<ListarOrdensServicoResponse> listar() {
    	var output = listarOrdensServico.executar();
        var response = ListarOrdensServicoResponse.from(output);
        return ResponseEntity.ok(response);      
      
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DetalharOrdemServicoResponse> detalhar(
            @Parameter(description = "ID da ordem de serviço", example = "55555555-5555-5555-5555-000000000003") @PathVariable String id) {

        DetalharOrdemServicoOutput output = detalharOrdemServico.executar(id);
        if (output == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(DetalharOrdemServicoResponse.from(output));
    }
    
    @GetMapping("/cliente/detalhamento")
    public ResponseEntity<DetalharOrdemServicoClienteResponse> detalharParaCliente(
            @Parameter(description = "Número da OS", example = "OS-2026-900003") @RequestParam String numeroOrdemServico,
            @Parameter(description = "Código de acesso enviado ao cliente", example = "JKLM2345") @RequestParam String codigoVerificacao,
            @Parameter(description = "CPF ou CNPJ do cliente", example = "\"52998224725\"", schema = @Schema(type = "string")) @RequestParam String cpfCnpj) {

        var input = new DetalharOrdemServicoClienteInput(
                numeroOrdemServico,
                codigoVerificacao,
                cpfCnpj
        );

        var output = detalharOrdemServicoCliente.executar(input);

        if (output == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                DetalharOrdemServicoClienteResponse.from(output)
        );
    }
    
    @GetMapping("/tempo-medio-execucao")
    @PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")
    public ResponseEntity<CalcularTempoMedioExecucaoResponse> calcularTempoMedioExecucao() {

        var output = calcularTempoMedioExecucao.executar();

        return ResponseEntity.ok(
                CalcularTempoMedioExecucaoResponse.from(output)
        );
    }
   
}