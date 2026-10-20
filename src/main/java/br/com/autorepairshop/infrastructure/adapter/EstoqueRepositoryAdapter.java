package br.com.autorepairshop.infrastructure.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.estoque.Estoque;
import br.com.autorepairshop.domain.estoque.EstoqueRepository;
import br.com.autorepairshop.infrastructure.persistence.EstoqueEntity;
import br.com.autorepairshop.infrastructure.repository.EstoqueJpaRepository;



@Repository
public class EstoqueRepositoryAdapter implements EstoqueRepository {

    private final EstoqueJpaRepository jpaRepository;

    public EstoqueRepositoryAdapter(
            EstoqueJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void salvar(Estoque estoque) {
        EstoqueEntity entity = EstoqueEntity.fromDomain(estoque);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Estoque> buscarPorInsumoId(UUID insumoId) {
        return jpaRepository.findByInsumoId(insumoId)
                .map(EstoqueEntity::toDomain);
    }

    @Override
    public Optional<Estoque> buscarParaAtualizacao(UUID insumoId) {
        return jpaRepository.buscarParaAtualizacao(insumoId)
                .map(EstoqueEntity::toDomain);
    }
}