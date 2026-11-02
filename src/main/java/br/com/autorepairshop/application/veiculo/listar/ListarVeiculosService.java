package br.com.autorepairshop.application.veiculo.listar;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.application.shared.dto.PaginacaoInput;
import br.com.autorepairshop.application.shared.dto.PaginacaoOutput;
import br.com.autorepairshop.application.veiculo.shared.VeiculoOutput;
import br.com.autorepairshop.domain.shared.Pagina;
import br.com.autorepairshop.domain.shared.valueobject.Placa;
import br.com.autorepairshop.domain.veiculo.Veiculo;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;

@Service
public class ListarVeiculosService implements ListarVeiculosUseCase {

    private final VeiculoRepository veiculoRepository;

    public ListarVeiculosService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ListarVeiculosOutput executar(ListarVeiculosInput input,PaginacaoInput paginacao) {

        
        String placa = null;

        if (input.placa() != null && !input.placa().isBlank()) {
            placa = Placa.criar(input.placa()).placa();
        }

        Pagina<Veiculo> resultado = veiculoRepository.buscar(
            placa,
            input.marca(),
            input.modelo(),
            input.anoFabricacao(),
            paginacao.toPaginacao()
        );

        List<VeiculoOutput> veiculos = resultado.itens().stream()
            .map(VeiculoOutput::fromDomain)
            .toList();

        return new ListarVeiculosOutput(veiculos, PaginacaoOutput.de(resultado));
    }
}