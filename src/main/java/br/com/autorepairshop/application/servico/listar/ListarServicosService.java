package br.com.autorepairshop.application.servico.listar;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.application.servico.shared.ServicoOutput;
import br.com.autorepairshop.application.shared.dto.PaginacaoInput;
import br.com.autorepairshop.application.shared.dto.PaginacaoOutput;
import br.com.autorepairshop.domain.servico.Servico;
import br.com.autorepairshop.domain.servico.ServicoRepository;
import br.com.autorepairshop.domain.shared.Pagina;

@Service
public class ListarServicosService implements ListarServicosUseCase {

    private final ServicoRepository servicoRepository;

    public ListarServicosService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ListarServicosOutput executar(ListarServicosInput input, PaginacaoInput paginacaoInput) {

        Pagina<Servico> resultado = servicoRepository.buscar(input.descricao(),input.valorReferencia(),paginacaoInput.paraPagina());

        List<ServicoOutput> servicos = resultado.itens().stream()
            .map(ServicoOutput::fromDomain)
            .toList();

        return new ListarServicosOutput(servicos, PaginacaoOutput.de(resultado));
           
    }
}