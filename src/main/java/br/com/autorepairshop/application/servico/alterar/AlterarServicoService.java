package br.com.autorepairshop.application.servico.alterar;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.servico.Servico;
import br.com.autorepairshop.domain.servico.ServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

@Service
public class AlterarServicoService implements AlterarServicoUseCase {

	private final ServicoRepository servicoRepository;

	public AlterarServicoService(ServicoRepository servicoRepository) {
		this.servicoRepository = servicoRepository;
	}

	@Override
	@Transactional
	public void executar(AlterarServicoInput input) {
		
		UUID id;

        try {
            id = UUID.fromString(input.id());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new DomainException(DomainError.ID_SERVICO_INVALIDO);
        }
        
		Optional<Servico> servico = servicoRepository.buscarPorId(id);
		if (servico.isEmpty()) {
            throw new DomainException(DomainError.SERVICO_NAO_ENCONTRADO);
        }

		servico.get().atualizarDados(input.descricao(), input.valorReferencia());
		servicoRepository.salvar(servico.get());

	}
}
