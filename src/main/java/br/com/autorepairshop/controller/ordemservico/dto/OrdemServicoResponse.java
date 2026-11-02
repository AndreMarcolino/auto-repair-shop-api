package br.com.autorepairshop.controller.ordemservico.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.autorepairshop.application.ordemservico.listar.ListarOrdensServicoOutput;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;

public record OrdemServicoResponse(
        UUID id,
        String numero,
        UUID clienteId,
        UUID veiculoId,
        StatusOrdemServico status,
        LocalDateTime dataAbertura
) {
    public static OrdemServicoResponse from(ListarOrdensServicoOutput output) {
        return new OrdemServicoResponse(
                output.id(),
                output.numero(),
                output.clienteId(),
                output.veiculoId(),
                output.status(),
                output.dataAbertura()
        );
    }
}