package br.com.autorepairshop.application.ordemservico.reprovar;

public record ReprovarOrdemServicoInput(
		String numeroOrdemServico,
        String codigoVerificacao,
        String cpfCnpj
) {}
