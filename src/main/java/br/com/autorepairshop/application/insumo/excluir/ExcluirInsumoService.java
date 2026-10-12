package br.com.autorepairshop.application.insumo.excluir;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.insumo.InsumoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class ExcluirInsumoService implements ExcluirInsumoUseCase {

    private final InsumoRepository insumoRepository;

    public ExcluirInsumoService(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    @Override
    @Transactional
    public void executar(String id) {
        UUID identificador;

        try {
            identificador = UUID.fromString(id);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new DomainException(DomainError.ID_INSUMO_INVALIDO);
        }

        var veiculo = insumoRepository.buscarPorId(identificador);

        if (veiculo.isEmpty()) {
            throw new DomainException(DomainError.INSUMO_NAO_ENCONTRADO);
        }

        insumoRepository.excluir(identificador);
    }
}

