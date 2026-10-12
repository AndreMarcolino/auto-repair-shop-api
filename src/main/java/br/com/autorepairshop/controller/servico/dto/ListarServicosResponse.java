
package br.com.autorepairshop.controller.servico.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.autorepairshop.application.servico.listar.ListarServicosOutput;
import br.com.autorepairshop.application.servico.shared.ServicoOutput;
import br.com.autorepairshop.controller.dto.PaginacaoResponse;

public record ListarServicosResponse(
        List<ServicoItemResponse> servicos,
        PaginacaoResponse paginacaoResponse
) {
    public static ListarServicosResponse paraResponse(
            ListarServicosOutput output
    ) {
        List<ServicoItemResponse> servicos = output.servicos()
                .stream()
                .map(ServicoItemResponse::paraResponse)
                .toList();

        return new ListarServicosResponse(
        		servicos, PaginacaoResponse.paraResponse(output.paginacao())
                
        );
    }

    public record ServicoItemResponse(
            UUID id,
            String descricao,
            BigDecimal valorReferencia,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        public static ServicoItemResponse paraResponse(ServicoOutput output) {
            return new ServicoItemResponse(
                    output.id(),
                    output.descricao(),
                    output.valorReferencia(),
                    output.criadoEm(),
                    output.atualizadoEm()
            );
        }
    }
}
