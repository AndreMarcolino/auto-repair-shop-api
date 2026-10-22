package br.com.autorepairshop.controller.cliente;

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

import br.com.autorepairshop.application.cliente.alterar.AlterarClienteUseCase;
import br.com.autorepairshop.application.cliente.buscarclientecpfcnpj.BuscarClientePorCpfCnpjUseCase;
import br.com.autorepairshop.application.cliente.buscarclienteporid.BuscarClientePorIdUseCase;
import br.com.autorepairshop.application.cliente.criar.CriarClienteUseCase;
import br.com.autorepairshop.application.cliente.desativarcliente.DesativarClienteUseCase;
import br.com.autorepairshop.application.cliente.listar.ListarClientesUseCase;
import br.com.autorepairshop.application.shared.dto.PaginacaoInput;
import br.com.autorepairshop.controller.cliente.dto.AlterarClienteRequest;
import br.com.autorepairshop.controller.cliente.dto.ClienteResponse;
import br.com.autorepairshop.controller.cliente.dto.CriarClienteRequest;
import br.com.autorepairshop.controller.cliente.dto.ListarClientesRequest;
import br.com.autorepairshop.controller.cliente.dto.ListarClientesResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Cliente", description = "Gestão administrativa CRUD de clientes")
@RestController
@RequestMapping("/api/clientes")

@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole(T(br.com.autorepairshop.infrastructure.security.Roles).ATENDENTE)")

public class ClienteController {

	private final CriarClienteUseCase criarCliente;

	private final AlterarClienteUseCase alterarCliente;

	private final ListarClientesUseCase listarClientes;

	private final BuscarClientePorCpfCnpjUseCase buscarClientePorCpfCnpj;

	private final DesativarClienteUseCase desativarCliente;
	
	private final BuscarClientePorIdUseCase buscarClientePorId;

	public ClienteController(CriarClienteUseCase clienteUser, AlterarClienteUseCase alterar,
			ListarClientesUseCase listarClientes, BuscarClientePorCpfCnpjUseCase buscarClientePorCpfCnpj,
			DesativarClienteUseCase desativarCliente,
			BuscarClientePorIdUseCase buscarClientePorId) {
		this.criarCliente = clienteUser;
		this.alterarCliente = alterar;
		this.listarClientes = listarClientes;
		this.desativarCliente = desativarCliente;
		this.buscarClientePorCpfCnpj = buscarClientePorCpfCnpj;
		this.buscarClientePorId=buscarClientePorId;
	}

	@PostMapping
	public ResponseEntity<?> criar(@Valid @RequestBody CriarClienteRequest request) {
		var clienteOutput = criarCliente.executar(request.toInput());
		return ResponseEntity.ok(clienteOutput.id().toString());
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> alterar(@PathVariable String id, @Valid @RequestBody AlterarClienteRequest request) {
		var input = request.toInput(id);
		alterarCliente.executar(input);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<ListarClientesResponse> listar(@ModelAttribute ListarClientesRequest request) {

		var inputRequest = request.toInput();
		var inputPaginacao = new PaginacaoInput(request.pagina(), request.tamanho());
		var listaClienteOutput = listarClientes.executar(inputRequest, inputPaginacao);
		var response = ListarClientesResponse.toResponse(listaClienteOutput);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/por-cpf-cnpj")
	public ResponseEntity<ClienteResponse> buscarPorCpfCnpj(@RequestParam Short tipoPessoa,
			@RequestParam String cpfCnpj) {

		return buscarClientePorCpfCnpj.executar(tipoPessoa, cpfCnpj).map(ClienteResponse::toResponse)
				.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable String id) {
		desativarCliente.executar(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ClienteResponse> buscarClientePorId(@RequestParam String id) {
		var output = this.buscarClientePorId.executar(id);
		if(output.isPresent())
		{
			return ResponseEntity.ok(ClienteResponse.toResponse(output.get())); 
		}
		return ResponseEntity.notFound().build();
	}

}
