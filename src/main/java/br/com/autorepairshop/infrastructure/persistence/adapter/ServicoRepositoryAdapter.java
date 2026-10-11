package br.com.autorepairshop.infrastructure.persistence.adapter;

import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.servico.Servico;
import br.com.autorepairshop.domain.servico.ServicoRepository;
import br.com.autorepairshop.infrastructure.persistence.entity.ServicoEntity;
import br.com.autorepairshop.infrastructure.persistence.repository.ServicoJpaRepository;

@Repository
public class ServicoRepositoryAdapter implements ServicoRepository {

    private final ServicoJpaRepository jpaRepository;

    public ServicoRepositoryAdapter(ServicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Servico salvar(Servico servico) {
        ServicoEntity entity = ServicoEntity.fromDomain(servico);
        ServicoEntity salvo = jpaRepository.save(entity);
        return salvo.toDomain();
    }
}
