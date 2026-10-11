package br.com.autorepairshop.controller;

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

import br.com.autorepairshop.application.cliente.buscaclientecpfcnpj.BuscarClientePorCpfCnpjUseCase;
import br.com.autorepairshop.application.cliente.create.CriarClienteUseCase;
import br.com.autorepairshop.application.cliente.desativacliente.DesativarClienteUseCase;
import br.com.autorepairshop.application.cliente.list.ListarClientesUseCase;
import br.com.autorepairshop.application.cliente.update.AlterarClienteUseCase;
import br.com.autorepairshop.controller.cliente.dto.AlterarClienteRequest;
import br.com.autorepairshop.controller.cliente.dto.ClienteResponse;
import br.com.autorepairshop.controller.cliente.dto.CriarClienteRequest;
import br.com.autorepairshop.controller.cliente.dto.ListarClientesRequest;
import br.com.autorepairshop.controller.cliente.dto.ListarClientesResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

	private final CriarClienteUseCase criarClienteUseCase;

	private final AlterarClienteUseCase alterarClienteUseCase;
	
	private final ListarClientesUseCase listarClientesUseCase;
	
	private final BuscarClientePorCpfCnpjUseCase buscarClientePorCpfCnpjUseCase;
	
	private final DesativarClienteUseCase desativarClienteUseCase;

	public ClienteController(CriarClienteUseCase clienteUserCase, 
			AlterarClienteUseCase alterarClienteUseCase,
			ListarClientesUseCase listarClientesUseCase,
			BuscarClientePorCpfCnpjUseCase buscarClientePorCpfCnpjUseCase,
			DesativarClienteUseCase desativarClienteUseCase) {
		this.criarClienteUseCase = clienteUserCase;
		this.alterarClienteUseCase = alterarClienteUseCase;
		this.listarClientesUseCase = listarClientesUseCase;
		this.desativarClienteUseCase = desativarClienteUseCase;
		this.buscarClientePorCpfCnpjUseCase = buscarClientePorCpfCnpjUseCase;
	}

	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody CriarClienteRequest request) {
		var clienteOutput = criarClienteUseCase.executar(request.paraInput());
		return ResponseEntity.ok(clienteOutput.id().toString());
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody AlterarClienteRequest request) {
			var input = request.paraInput(id);
			alterarClienteUseCase.executar(input);
			return ResponseEntity.noContent().build();
		}
	
	 @GetMapping
	    public ResponseEntity<ListarClientesResponse> listar(
	        @ModelAttribute ListarClientesRequest request
	    ) {
		 
	        var input = request.paraInput();
		    var listaClienteOutput = listarClientesUseCase.executar(input);
		    var response = ListarClientesResponse.paraResponse(listaClienteOutput);	       
		    return ResponseEntity.ok(response);
	    }
	 
	 @GetMapping("/cpf-cnpj")
	 public ResponseEntity<ClienteResponse> buscarPorCpfCnpj(
	         @RequestParam Short tipoPessoa,
	         @RequestParam String cpfCnpj) {

	     return buscarClientePorCpfCnpjUseCase.executar(tipoPessoa, cpfCnpj)
	         .map(ClienteResponse::paraResponse)
	         .map(ResponseEntity::ok)
	         .orElseGet(() -> ResponseEntity.notFound().build());
	 }
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<Void> excluir(@PathVariable String id) {
	     desativarClienteUseCase.executar(id);
	     return ResponseEntity.noContent().build();
	 }
}
