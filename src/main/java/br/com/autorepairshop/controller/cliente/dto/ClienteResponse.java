package br.com.autorepairshop.controller.cliente.dto;

import br.com.autorepairshop.application.cliente.shared.ClienteOutput;

public record ClienteResponse(
	    String id,
	    String nome,
	    String cpfCnpj,
	    Short tipoPessoa,
	    String email,
	    String telefone,
	    boolean ativo
	) {
	
	public static ClienteResponse toResponse(ClienteOutput output) {
        if (output == null) {
            return null;
        }

        return new ClienteResponse(
            output.id(),
            output.nome(),
            output.cpfCnpj(),
            output.tipoPessoa(),
            output.email(),
            output.telefone(),
            output.ativo()
        );
    }
	
}
