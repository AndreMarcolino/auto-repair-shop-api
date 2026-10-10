package br.com.autorepairshop.application.veiculo.lista;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.application.veiculo.shared.VeiculoOutput;
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
    public ListarVeiculosOutput executar(ListarVeiculosInput input) {

        Pageable pageable = PageRequest.of(
            input.pagina(),
            input.tamanho(),
            Sort.by("marca").ascending()
        );

        String placa = null;

        if (input.placa() != null && !input.placa().isBlank()) {
            placa = Placa.criar(input.placa()).placa();
        }

        Page<Veiculo> resultado = veiculoRepository.buscar(
            placa,
            input.marca(),
            input.modelo(),
            input.anoFabricacao(),
            pageable
        );

        List<VeiculoOutput> veiculos = resultado
            .map(VeiculoOutput::fromDomain)
            .getContent();

        return new ListarVeiculosOutput(
            veiculos,
            resultado.getNumber(),
            resultado.getSize(),
            resultado.getTotalElements(),
            resultado.getTotalPages()
        );
    }
}