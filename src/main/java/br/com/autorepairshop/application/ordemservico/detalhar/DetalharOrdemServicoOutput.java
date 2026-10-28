package br.com.autorepairshop.application.ordemservico.detalhar;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;

public record DetalharOrdemServicoOutput(
        UUID id,
        String numero,
        StatusOrdemServico status,
        String problemaRelatado,
        LocalDateTime dataAbertura,
        LocalDateTime inicioServico,
        LocalDateTime fimServico,
        LocalDateTime dataEntrega,
        ClienteOutput cliente,
        VeiculoOutput veiculo,
        OrcamentoOutput orcamento
) {

    public record ClienteOutput(
            UUID id,
            String nome
    ) {}

    public record VeiculoOutput(
            UUID id,
            String placa,
            String marca,
            String modelo
    ) {}

    public record OrcamentoOutput(
            String diagnostico,
            BigDecimal valorTotal,
            List<ItemServicoOutput> servicos,
            List<ItemInsumoOutput> insumos
    ) {}

    public record ItemServicoOutput(
            String descricao,
            BigDecimal quantidade,
            BigDecimal valorUnitario,
            BigDecimal valorTotal
    ) {}

    public record ItemInsumoOutput(
            String descricao,
            BigDecimal quantidade,
            BigDecimal valorUnitario,
            BigDecimal valorTotal
    ) {}
}