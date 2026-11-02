package br.com.autorepairshop.application.ordemservico.detalharcliente;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;

public record DetalharOrdemServicoClienteOutput(
        String numero,
        StatusOrdemServico status,
        String problemaRelatado,
        LocalDateTime dataAbertura,
        LocalDateTime dataInicioServico,
        LocalDateTime dataFimServico,
        LocalDateTime dataEntrega,
        ClienteOutput cliente,
        VeiculoOutput veiculo,
        OrcamentoOutput orcamento
) {

    public record ClienteOutput(
            String nome
    ) {}

    public record VeiculoOutput(
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