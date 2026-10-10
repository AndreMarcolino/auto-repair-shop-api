package br.com.autorepairshop.application.veiculo.update;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.Placa;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;

@Service
public class AlterarVeiculoService implements AlterarVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;

    public AlterarVeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    @Transactional
    public void executar(AlterarVeiculoInput input) {

        UUID id;

        try {
            id = UUID.fromString(input.id());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new DomainException(DomainError.ID_VEICULO_INVALIDO);
        }

        Placa placa = Placa.criar(input.placa());

        var veiculo = veiculoRepository.buscarPorId(id);

        if (veiculo.isEmpty()) {
            throw new DomainException(DomainError.VEICULO_NAO_ENCONTRADO);
        }

        // Só verifica duplicidade se a placa mudou.
        if (!veiculo.get().getPlaca().equals(placa)
                && veiculoRepository.existePlaca(placa)) {

            throw new DomainException(DomainError.PLACA_VEICULO_JA_CADASTRADA);
        }

        veiculo.get().atualizarCadastro(
            placa,
            input.marca(),
            input.modelo(),
            input.anoFabricacao()
        );

        veiculoRepository.salvar(veiculo.get());
    }
}