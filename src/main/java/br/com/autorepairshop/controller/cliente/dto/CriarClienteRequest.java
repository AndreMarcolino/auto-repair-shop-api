package br.com.autorepairshop.controller.cliente.dto;

import br.com.autorepairshop.application.cliente.criar.CriarClienteInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record CriarClienteRequest(
	    @Schema(description = "Nome do cliente", example = "Pedro Santos")
	    String nome,
	    @Schema(description = "1 = Pessoa física, 2 = Pessoa jurídica", example = "1")
	    Short tipoPessoa,
	    @Schema(description = "CPF (11 dígitos) ou CNPJ (14 caracteres)", example = "\"39053344705\"", type = "string")
	    String cpfCnpj,
	    @Schema(description = "Telefone com DDD", example = "\"11912345678\"", type = "string")
	    String telefone,
	    @Schema(description = "E-mail do cliente", example = "pedro.santos@email.com")
	    String email
	) {
	    public CriarClienteInput toInput() {
	        return new CriarClienteInput(
	            nome,
	            tipoPessoa,
	            cpfCnpj,
	            telefone,
	            email
	        );
	    }
	}
