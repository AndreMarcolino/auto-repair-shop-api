package br.com.autorepairshop.application.veiculo.create;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.Placa;
import br.com.autorepairshop.domain.veiculo.Veiculo;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;

@Service
public class CriarVeiculoService implements CriarVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;

    public CriarVeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    @Transactional
    public CriarVeiculoOutput executar(
        CriarVeiculoInput input) 
    {
        Placa placa = Placa.criar(input.placa());

        if (veiculoRepository.existePlaca(placa)) {
        	throw new DomainException(DomainError.PLACA_VEICULO_JA_CADASTRADA);
        }
        Veiculo veiculo = Veiculo.criar(
            placa,
            input.marca(),
            input.modelo(),
            input.anoFabricacao()
        );

        Veiculo salvo = veiculoRepository.salvar(veiculo);

        CriarVeiculoOutput output = new CriarVeiculoOutput(salvo.getId());
        return output;
        
    }
}