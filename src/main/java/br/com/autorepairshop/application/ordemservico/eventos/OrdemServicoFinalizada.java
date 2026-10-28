package br.com.autorepairshop.application.ordemservico.eventos;

import java.util.UUID;

public record OrdemServicoFinalizada(
        UUID ordemServicoId
) {}