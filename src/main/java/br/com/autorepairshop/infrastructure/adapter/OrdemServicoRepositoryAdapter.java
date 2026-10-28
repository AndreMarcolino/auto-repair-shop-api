package br.com.autorepairshop.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;
import br.com.autorepairshop.infrastructure.persistence.OrdemServicoEntity;
import br.com.autorepairshop.infrastructure.repository.OrdemServicoJpaRepository;

@Repository
class OrdemServicoRepositoryAdapter implements OrdemServicoRepository {

    private final OrdemServicoJpaRepository jpaRepository;

    public OrdemServicoRepositoryAdapter(
            OrdemServicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void salvar(OrdemServico ordemServico) {
        OrdemServicoEntity entity =
                OrdemServicoEntity.toEntity(ordemServico);

        jpaRepository.save(entity);
    }

    @Override
    public Optional<OrdemServico> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(OrdemServicoEntity::toDomain);
    }

	@Override
	public List<OrdemServico> buscarPorStatus(StatusOrdemServico status) {
		return this.jpaRepository.findByStatus(status.getId()).stream().map(OrdemServicoEntity::toDomain).toList();
	}
	

    @Override
    public long proximoNumero() {
        return jpaRepository.proximoNumero();
    }
    
    @Override
    public List<OrdemServico> buscarPorStatus(
            List<StatusOrdemServico> status) {

        List<Short> idsStatus = status.stream()
                .map(StatusOrdemServico::getId)
                .toList();

        return jpaRepository.findByStatusIn(idsStatus)
                .stream()
                .map(OrdemServicoEntity::toDomain)
                .toList();
    }

	@Override
	public Optional<OrdemServico> buscarPoNumero(String numero) {
		return this.jpaRepository.findByNumero(numero).map(OrdemServicoEntity::toDomain);
		
	}
}