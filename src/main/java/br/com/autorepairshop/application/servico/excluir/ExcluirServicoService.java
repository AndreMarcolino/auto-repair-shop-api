package br.com.autorepairshop.application.servico.excluir;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.servico.ServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class ExcluirServicoService implements ExcluirServicoUseCase {

    private final ServicoRepository servicoRepository;

    public ExcluirServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Override
    @Transactional
    public void executar(String id) {
        UUID identificador;

        try {
            identificador = UUID.fromString(id);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new DomainException(DomainError.ID_SERVICO_INVALIDO);
        }

        var veiculo = servicoRepository.buscarPorId(identificador);

        if (veiculo.isEmpty()) {
            throw new DomainException(DomainError.SERVICO_NAO_ENCONTRADO);
        }

        servicoRepository.excluir(identificador);
    }
}

