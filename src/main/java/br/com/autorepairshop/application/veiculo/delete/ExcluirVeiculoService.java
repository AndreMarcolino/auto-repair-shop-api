package br.com.autorepairshop.application.veiculo.delete;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;

@Service
public class ExcluirVeiculoService implements ExcluirVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;

    public ExcluirVeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    @Transactional
    public void executar(String id) {
        UUID identificador;

        try {
            identificador = UUID.fromString(id);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new DomainException(DomainError.ID_VEICULO_INVALIDO);
        }

        var veiculo = veiculoRepository.buscarPorId(identificador);

        if (veiculo.isEmpty()) {
            throw new DomainException(DomainError.VEICULO_NAO_ENCONTRADO);
        }

        veiculoRepository.excluir(identificador);
    }
}

