package br.com.autorepairshop.application.ordemservico.aprovar;

public record AprovarOrdemServicoInput(
        String numeroOrdemServico,
        String codigoVerificacao,
        String cpfCnpj
) {
}