package br.com.autorepairshop.application.ordemservico.listar;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;

public record ListarOrdensServicoOutput(
        UUID id,
        String numero,
        UUID clienteId,
        UUID veiculoId,
        StatusOrdemServico status,
        LocalDateTime dataAbertura
) {
}