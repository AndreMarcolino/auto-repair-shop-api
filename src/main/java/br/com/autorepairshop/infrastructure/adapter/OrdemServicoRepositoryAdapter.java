package br.com.autorepairshop.infrastructure.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
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
}