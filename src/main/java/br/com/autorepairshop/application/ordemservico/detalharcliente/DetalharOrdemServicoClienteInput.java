package br.com.autorepairshop.application.ordemservico.detalharcliente;

public record DetalharOrdemServicoClienteInput(String numeroOrdemServico,
        String codigoVerificacao,
        String cpfCnpj) {
	
}
