package br.com.autorepairshop.controller.cliente.dto;

import br.com.autorepairshop.application.cliente.alterar.AlterarClienteInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record AlterarClienteRequest(
	    @Schema(description = "Nome do cliente", example = "João da Silva Junior")
	    String nome,
	    @Schema(description = "1 = Pessoa física, 2 = Pessoa jurídica", example = "1")
	    Short tipoPessoa,
	    @Schema(description = "CPF (11 dígitos) ou CNPJ (14 caracteres)", example = "\"52998224725\"", type = "string")
	    String cpfCnpj,
	    @Schema(description = "Telefone com DDD", example = "\"11999998888\"", type = "string")
	    String telefone,
	    @Schema(description = "E-mail do cliente", example = "joao.silva.jr@email.com")
	    String email
	) {
	
	public AlterarClienteInput toInput(String id) {
        return new AlterarClienteInput(
            id,
            nome,
            tipoPessoa,
            cpfCnpj,
            telefone,
            email
        );
    }	
	
}


