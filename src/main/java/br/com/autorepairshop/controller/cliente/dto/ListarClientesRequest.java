package br.com.autorepairshop.controller.cliente.dto;

import br.com.autorepairshop.application.cliente.listar.ListarClientesInput;
import io.swagger.v3.oas.annotations.media.Schema;

public record ListarClientesRequest(
	    @Schema(description = "Filtro por parte do nome", example = "Silva")
	    String nome,
	    @Schema(description = "1 = Pessoa física, 2 = Pessoa jurídica", example = "1")
	    Short tipoPessoa,
	    @Schema(description = "CPF ou CNPJ", example = "\"52998224725\"", type = "string")
	    String cpfCnpj,
	    @Schema(description = "E-mail exato", example = "joao.silva@email.com")
	    String email,
	    @Schema(description = "Telefone com DDD", example = "\"11987654321\"", type = "string")
	    String telefone,
	    @Schema(description = "Somente ativos (true) ou inativos (false)", example = "true")
	    Boolean ativo,
	    @Schema(description = "Número da página (começa em 0)", example = "0")
	    int pagina,
        @Schema(description = "Quantidade de itens por página", example = "10")
        int tamanho
	) {
	
	 public ListarClientesInput toInput() {
	        return new ListarClientesInput(
	            nome,
	            tipoPessoa,
	            cpfCnpj,
	            email,
	            ativo,
	            telefone
	        );
	    }
}
