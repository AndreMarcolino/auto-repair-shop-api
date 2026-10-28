package br.com.autorepairshop.application.ordemservico.abrirordemservico;

public record AbrirOrdemServicoInput(String idCliente, String idVeiculo, String problemaRelatado) {
}