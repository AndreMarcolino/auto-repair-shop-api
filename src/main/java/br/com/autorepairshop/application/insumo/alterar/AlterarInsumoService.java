package br.com.autorepairshop.application.insumo.alterar;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.insumo.Insumo;
import br.com.autorepairshop.domain.insumo.InsumoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class AlterarInsumoService implements AlterarInsumoUseCase {

    private final InsumoRepository insumoRepository;

    public AlterarInsumoService(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    @Override
    @Transactional
    public void executar(AlterarInsumoInput input) {

        UUID id;

        try {
            id = UUID.fromString(input.id());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new DomainException(DomainError.ID_INSUMO_INVALIDO);
        }

        Optional<Insumo> insumo = insumoRepository.buscarPorId(id);

        if (insumo.isEmpty()) {
            throw new DomainException(DomainError.INSUMO_NAO_ENCONTRADO);
        }

        insumo.get().atualizarDados(
                input.descricao(),
                input.valorReferencia(),
                input.quantidadeEstoque());

        insumoRepository.salvar(insumo.get());
    }
}