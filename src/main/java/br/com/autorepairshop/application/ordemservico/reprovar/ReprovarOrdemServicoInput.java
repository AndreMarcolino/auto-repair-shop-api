package br.com.autorepairshop.application.ordemservico.reprovar;

public record ReprovarOrdemServicoInput(
        String idOrdemServico,
        Short tipoPessoa,
        String cpfCnpj
) {}
