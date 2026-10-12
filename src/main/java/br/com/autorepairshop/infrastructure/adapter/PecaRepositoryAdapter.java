package br.com.autorepairshop.infrastructure.adapter;

import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.peca.Peca;
import br.com.autorepairshop.domain.peca.PecaRepository;
import br.com.autorepairshop.infrastructure.persistence.PecaEntity;
import br.com.autorepairshop.infrastructure.repository.PecaJpaRepository;

@Repository
class PecaRepositoryAdapter implements PecaRepository {

    private final PecaJpaRepository jpaRepository;

    public PecaRepositoryAdapter(PecaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Peca salvar(Peca peca) {
        PecaEntity entity = PecaEntity.fromDomain(peca);
        PecaEntity salva = jpaRepository.save(entity);
        return salva.paraDominio();
    }
}
