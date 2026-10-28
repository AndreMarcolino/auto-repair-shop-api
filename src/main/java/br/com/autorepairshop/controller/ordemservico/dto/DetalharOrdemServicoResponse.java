package br.com.autorepairshop.controller.ordemservico.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.autorepairshop.application.ordemservico.detalhar.DetalharOrdemServicoOutput;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;

public record DetalharOrdemServicoResponse(
        UUID id,
        String numero,
        StatusOrdemServico status,
        String problemaRelatado,
        LocalDateTime dataAbertura,
        LocalDateTime dataInicioServico,
        LocalDateTime dataFimServico,
        LocalDateTime dataEntrega,
        ClienteResponse cliente,
        VeiculoResponse veiculo,
        OrcamentoResponse orcamento
) {

    public record ClienteResponse(
            UUID id,
            String nome
    ) {}

    public record VeiculoResponse(
            UUID id,
            String placa,
            String marca,
            String modelo
    ) {}

    public record OrcamentoResponse(
            String diagnostico,
            BigDecimal valorTotal,
            List<ItemServicoResponse> servicos,
            List<ItemInsumoResponse> insumos
    ) {}

    public record ItemServicoResponse(
            String descricao,
            BigDecimal quantidade,
            BigDecimal valorUnitario,
            BigDecimal valorTotal
    ) {}

    public record ItemInsumoResponse(
            String descricao,
            BigDecimal quantidade,
            BigDecimal valorUnitario,
            BigDecimal valorTotal
    ) {}

    public static DetalharOrdemServicoResponse from(
            DetalharOrdemServicoOutput output) {

        return new DetalharOrdemServicoResponse(
                output.id(),
                output.numero(),
                output.status(),
                output.problemaRelatado(),
                output.dataAbertura(),
                output.inicioServico(),
                output.fimServico(),
                output.dataEntrega(),
                new ClienteResponse(
                        output.cliente().id(),
                        output.cliente().nome()
                ),
                new VeiculoResponse(
                        output.veiculo().id(),
                        output.veiculo().placa(),
                        output.veiculo().marca(),
                        output.veiculo().modelo()
                ),
                paraOrcamentoResponse(output.orcamento())
        );
    }

    private static OrcamentoResponse paraOrcamentoResponse(
            DetalharOrdemServicoOutput.OrcamentoOutput output) {

        if (output == null) {
            return null;
        }

        List<ItemServicoResponse> servicos = output.servicos().stream()
                .map(item -> new ItemServicoResponse(
                        item.descricao(),
                        item.quantidade(),
                        item.valorUnitario(),
                        item.valorTotal()
                ))
                .toList();

        List<ItemInsumoResponse> insumos = output.insumos().stream()
                .map(item -> new ItemInsumoResponse(
                        item.descricao(),
                        item.quantidade(),
                        item.valorUnitario(),
                        item.valorTotal()
                ))
                .toList();

        return new OrcamentoResponse(
                output.diagnostico(),
                output.valorTotal(),
                servicos,
                insumos
        );
    }
}