package br.com.autorepairshop.infrastructure.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.ordemservico.OrdemServicoInsumoPendente;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoInsumoPendenteRepository;
import br.com.autorepairshop.infrastructure.persistence.OrdemServicoInsumoPendenteEntity;
import br.com.autorepairshop.infrastructure.repository.OrdemServicoInsumoPendenteJpaRepository;

@Repository
public class OrdemServicoInsumoPendenteRepositoryAdapter
        implements OrdemServicoInsumoPendenteRepository {

    private final OrdemServicoInsumoPendenteJpaRepository jpaRepository;

    public OrdemServicoInsumoPendenteRepositoryAdapter(
            OrdemServicoInsumoPendenteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void salvar(OrdemServicoInsumoPendente pendente) {
        var entity = jpaRepository.findById(pendente.getId())
                .map(existente -> {
                    existente.atualizarDados(pendente);
                    return existente;
                })
                .orElseGet(() ->
                        OrdemServicoInsumoPendenteEntity.fromDomain(pendente));

        jpaRepository.save(entity);
    }

    @Override
    public List<OrdemServicoInsumoPendente> buscarPorOrdemServicoId(
            UUID ordemServicoId) {
        return jpaRepository.findByOrdemServicoId(ordemServicoId)
                .stream()
                .map(OrdemServicoInsumoPendenteEntity::toDomain)
                .toList();
    }

    @Override
    public void excluirPorOrdemServicoId(UUID ordemServicoId) {
        jpaRepository.deleteByOrdemServicoId(ordemServicoId);
    }
}